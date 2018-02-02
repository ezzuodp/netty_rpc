package com.ezweb.engine.client;

import com.ezweb.engine.CustTMessage;
import com.ezweb.engine.CustTType;
import com.ezweb.engine.NettyDecoder;
import com.ezweb.engine.NettyEncoder;
import com.ezweb.engine.exception.TSendRequestException;
import com.ezweb.engine.exception.TTimeoutException;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.concurrent.*;

/**
 * @author : zuodp
 * @version : 1.10
 */
public class NettyClient {
	private final static Logger logger = LoggerFactory.getLogger(NettyClient.class);

	private final Bootstrap bootstrap = new Bootstrap();

	private final EventLoopGroup eventLoopGroupWorker;
	//private DefaultEventExecutorGroup defaultEventExecutorGroup;

	private Channel channel = null;
	// 缓存所有对外请求
	protected final ConcurrentHashMap<Integer, ResponseFuture> responseTable = new ConcurrentHashMap<>(256);
	// 心跳定时器
	private ScheduledExecutorService heatbeatExe;

	public NettyClient() {
		this.eventLoopGroupWorker = new NioEventLoopGroup(1, new DefaultThreadFactory("NettyClientSelector"));
		//this.defaultEventExecutorGroup = new DefaultEventExecutorGroup(16, new DefaultThreadFactory("NettyClientWorkerThread"));
	}

	public void open(String inetHost, int inetPort) {
		Bootstrap handler = this.bootstrap.group(this.eventLoopGroupWorker)
				.channel(NioSocketChannel.class)
				.option(ChannelOption.TCP_NODELAY, true)
				.option(ChannelOption.SO_KEEPALIVE, false)
				.handler(new ChannelInitializer<SocketChannel>() {
					@Override
					public void initChannel(SocketChannel ch) throws Exception {
						ch.pipeline().addLast(
								//defaultEventExecutorGroup,
								new LoggingHandler("com.ezweb.CLIENT", LogLevel.DEBUG),
								new NettyDecoder(),
								new NettyEncoder(),
								new NettyClientHandler()
						);
					}
				});
		ChannelFuture channelFuture = handler.connect(inetHost, inetPort);
		this.channel = channelFuture.channel();
		try {
			channelFuture.sync();
			logger.info("connect {}:{} ok.", inetHost, inetPort);

			heatbeatExe = Executors.newScheduledThreadPool(1, new DefaultThreadFactory("netty-client-heartbeat", true));
			heatbeatExe.scheduleAtFixedRate(new NettyHeartbeatTask(), 30, 30, TimeUnit.SECONDS);
		} catch (Exception e) {
			logger.error("connect {}:{} error：", inetHost, inetPort, e);
		}
	}

	public void close() {
		logger.info("close channel:{}", this.channel);
		heatbeatExe.shutdown();
		Future<?> f = this.eventLoopGroupWorker.shutdownGracefully();
		while (!f.isDone()) {
			try {
				f.get(100, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
				break;
			} catch (Throwable e) {
				// ingore e;
			}
		}


		/*if (this.defaultEventExecutorGroup != null) {
			f = this.defaultEventExecutorGroup.shutdownGracefully();
			while (!f.isDone()) {
				try {
					f.get(100, TimeUnit.MILLISECONDS);
				} catch (InterruptedException e) {
					break;
				} catch (Throwable e) {
					// ingore e;
				}
			}
		}*/

		this.channel.close();

		logger.info("close channel:{} ok.", this.channel);
	}

	class NettyClientHandler extends SimpleChannelInboundHandler<CustTMessage> {

		@Override
		protected void channelRead0(ChannelHandlerContext ctx, CustTMessage msg) throws Exception {
			int id = msg.getSeqId();
			ResponseFuture responseFuture = responseTable.get(id);
			if (responseFuture != null) {
				logger.debug("receive request id:{} response data.", id);

				// TODO:可以检查返回的msg.type == REPLY

				responseFuture.setResponse(msg);
				responseFuture.release();           // 发出通知,可以读取response了.

				responseTable.remove(id);
			} else {
				logger.warn("receive request id:{} response, but it's not in.", id);
			}
		}
	}

	class NettyHeartbeatTask implements Runnable {
		private byte[] PING = ("PING").getBytes();

		public NettyHeartbeatTask() {
		}

		@Override
		public void run() {
			final CustTMessage request = CustTMessage.newRequestMessage();
			request.setType(CustTType.ONEWAY);
			request.setBody(ByteBuffer.wrap(PING));

			channel.writeAndFlush(request).addListener((ChannelFutureListener) future -> {
				if (future.isSuccess()) {
					return;
				}
				logger.warn("send Heartbeat to channel <{}> failed.\nREQ:{}", future.channel(), request);
			});
		}
	}

	public CustTMessage writeReq(final CustTMessage request, final int timeout) throws InterruptedException, TTimeoutException, TSendRequestException {
		ResponseFuture responseFuture = writeReqImpl(request);
		CustTMessage response = responseFuture.get(timeout, TimeUnit.MILLISECONDS);
		if (null == response) {
			if (responseFuture.isDone()) {
				throw new TTimeoutException(
						String.format("wait response on the channel <%s> timeout %d(milliseconds).", channel, timeout)
				);
			} else {
				throw new TSendRequestException(
						String.format("send request to the channel <%s> failed.", channel)
				);
			}
		} else {
			logger.debug("send a request to channel <{}> success.\nREQ:{}\nRES:{}", channel, request, response);
		}
		return response;
	}

	//public CompletableFuture<CustTMessage> writeReqAsync(final CustTMessage request, final int timeout) {
	// guava 代码，使用线程池.
		/*ResponseFuture responseFuture = writeReqImpl(request);
		return Futures.transformAsync(
				Futures.immediateFuture(responseFuture),
				new AsyncFunction<ResponseFuture, CustTMessage>() {
					@Override
					public ListenableFuture<CustTMessage> apply(ResponseFuture responseFuture) throws Exception {
						CustTMessage response = responseFuture.waitResponse(timeout, TimeUnit.MILLISECONDS);
						if (null == response) {
							if (responseFuture.isOk()) {
								throw new TTimeoutException(
										String.format("wait response on the channel <%s> timeout %d(milliseconds).", channel, timeout)
								);
							} else {
								throw new TSendRequestException(
										String.format("send request to the channel <%s> failed.", channel)
								);
							}
						} else {
							logger.debug("send a request to channel <{}> success.\nREQ:{}\nRES:{}", channel, request, response);
						}
						return Futures.immediateFuture(response);
					}
				},
				asyc_thread_pool);*/
	// java8
		/*
		CompletableFuture<CustTMessage> future = CompletableFuture.supplyAsync(() -> {
			ResponseFuture responseFuture = writeReqImpl(request);

			CustTMessage response = null;
			try {
				response = responseFuture.waitResponse(timeout, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
				// ignore e;
			}
			if (null == response) {
				if (responseFuture.isOk()) {
					throw new TTimeoutException(
							String.format("wait response on the channel <%s> timeout %d(milliseconds).", channel, timeout)
					);
				} else {
					throw new TSendRequestException(
							String.format("send request to the channel <%s> failed.", channel)
					);
				}
			} else {
				logger.debug("send a request to channel <{}> success.\nREQ:{}\nRES:{}", channel, request, response);
				return response;
			}
		});
		return future;
		*/
	//}

	private ResponseFuture writeReqImpl(CustTMessage request) {
		if (!channel.isActive()) throw new TSendRequestException("channel is closed.");

		final ResponseFuture responseFuture = new ResponseFuture(request.getSeqId());
		responseTable.put(responseFuture.getId(), responseFuture);
		channel.writeAndFlush(request).addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture nettyFuture) throws Exception {
				if (nettyFuture.isSuccess()) {
					responseFuture.setIsOk(true);
					return;
				} else {
					responseFuture.setIsOk(false);
				}

				// 写入失败了,就从缓存中移掉这个请求
				responseTable.remove(responseFuture.getId(), responseFuture);

				responseFuture.setResponse(null);
				logger.warn("send a request to channel <{}> failed.\nREQ:{}", nettyFuture.channel(), request);
			}
		});
		return responseFuture;
	}

}
