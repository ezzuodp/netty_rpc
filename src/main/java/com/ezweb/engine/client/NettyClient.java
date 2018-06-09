package com.ezweb.engine.client;

import com.ezweb.engine.CustTMessage;
import com.ezweb.engine.CustTMessageDecoder;
import com.ezweb.engine.CustTMessageEncoder;
import com.ezweb.engine.CustTType;
import com.ezweb.engine.exception.TSendRequestException;
import com.ezweb.engine.exception.TSerializeException;
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

import java.io.Closeable;
import java.util.concurrent.*;

/**
 * @author : zuodp
 * @version : 1.10
 */
public class NettyClient implements Closeable {
	private final static Logger logger = LoggerFactory.getLogger(NettyClient.class);

	private final Bootstrap bootstrap = new Bootstrap();

	private final EventLoopGroup eventLoopGroupWorker;
	//private DefaultEventExecutorGroup defaultEventExecutorGroup;

	private Channel channel = null;
	// 缓存所有对外请求
	protected final ConcurrentHashMap<Integer, Future<CustTMessage>> responseTable = new ConcurrentHashMap<>(256);
	// 心跳定时器
	private ScheduledExecutorService heatbeatExe;

	public NettyClient() {
		this.eventLoopGroupWorker = new NioEventLoopGroup(1, new DefaultThreadFactory("NettyClientSelector"));
		//this.defaultEventExecutorGroup = new DefaultEventExecutorGroup(16, new DefaultThreadFactory("NettyClientWorkerThread"));
	}

	public void connect(String inetHost, int inetPort) throws Exception {
		Bootstrap handler = this.bootstrap.group(this.eventLoopGroupWorker)
				.channel(NioSocketChannel.class)
				.option(ChannelOption.TCP_NODELAY, true)
				.option(ChannelOption.SO_KEEPALIVE, false)
				.handler(new ChannelInitializer<SocketChannel>() {
					@Override
					public void initChannel(SocketChannel ch) throws Exception {
						ch.pipeline().addLast(
								//defaultEventExecutorGroup,
								new LoggingHandler(NettyClient.class.getName(), LogLevel.DEBUG),
								new CustTMessageDecoder(),
								new CustTMessageEncoder(),
								new NettyClientHandler()
						);
					}
				});
		ChannelFuture channelFuture = handler.connect(inetHost, inetPort);
		this.channel = channelFuture.channel();
		try {
			channelFuture.sync();
			logger.info("connect {}:{} ok.", inetHost, inetPort);

		} catch (Exception e) {
			logger.error("connect {}:{} error：", inetHost, inetPort, e);
			throw e;
		}

		heatbeatExe = Executors.newScheduledThreadPool(1, new DefaultThreadFactory("netty-client-heartbeat", true));
		heatbeatExe.scheduleAtFixedRate(new NettyHeartbeatTask(), 30, 30, TimeUnit.SECONDS);
	}

	@Override
	public void close() {
		logger.info("close channel:{}", this.channel);
		this.channel.close();

		if (heatbeatExe != null) heatbeatExe.shutdown();

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


		logger.info("close channel:{} ok.", this.channel);
	}

	class NettyClientHandler extends SimpleChannelInboundHandler<CustTMessage> {
		@Override
		protected void channelRead0(ChannelHandlerContext ctx, CustTMessage msg) {
			int id = msg.getSeqId();
			Future<CustTMessage> responseFuture = responseTable.remove(id);
			if (responseFuture != null) {
				logger.debug("receive request id:{} response data.", id);
				((ResponseFutureV2) responseFuture).set(msg);
			} else {
				logger.warn("receive request id:{} response, but it's not in.", id);
			}
		}

	}

	class NettyHeartbeatTask implements Runnable {
		public NettyHeartbeatTask() {
		}

		@Override
		public void run() {
			final CustTMessage request = CustTMessage.newRequestMessage();
			request.setType(CustTType.HEARTBEAT);
			request.setBody(null);
			request.setLen(0);

			channel.writeAndFlush(request).addListener((ChannelFutureListener) future -> {
				if (future.isSuccess()) {
					logger.debug("send Heartbeat to channel <{}> success.\nREQ:{}", future.channel(), request);
					return;
				}
				logger.warn("send Heartbeat to channel <{}> failed.\nREQ:{}", future.channel(), request);
			});
		}
	}

	public CustTMessage writeReq(final CustTMessage request, final int timeout) throws TTimeoutException, TSendRequestException, TSerializeException {

		try {
			Future<CustTMessage> responseFuture = writeReqImpl(request);
			CustTMessage response = responseFuture.get(timeout, TimeUnit.MILLISECONDS);

			logger.debug("send a request to channel <{}> success.\nREQ:{}\nRES:{}", channel, request, response);

			return response;
		} catch (TimeoutException e) {
			throw new TTimeoutException(String.format("wait response on the channel <%s> timeout %d(milliseconds).", channel, timeout), e);
		} catch (InterruptedException | ExecutionException e) {
			throw new TSendRequestException(String.format("send request to the channel <%s> failed", channel), e);
		}
	}

	private Future<CustTMessage> writeReqImpl(CustTMessage request) {
		if (!channel.isActive()) throw new TSendRequestException("channel is closed.");

		final ResponseFutureV2 responseFuture = new ResponseFutureV2(request.getSeqId());
		responseTable.put(request.getSeqId(), responseFuture);

		channel.writeAndFlush(request).addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture nettyFuture) throws Exception {
				if (nettyFuture.isSuccess()) {
					logger.debug("write a request to channel <{}> success.\nREQ:{}", nettyFuture.channel(), request);
				} else {
					logger.error("write a request to channel <{}> failed.\nREQ:{}", nettyFuture.channel(), request);

					boolean cancel = responseFuture.cancel(true);
					if (cancel) {
						responseTable.remove(responseFuture.getSeqId());
					}
				}
			}
		});
		return responseFuture;
	}

}
