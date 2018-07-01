package com.ezweb.engine.server;

import com.ezweb.engine.CustTMessageDecoder;
import com.ezweb.engine.CustTMessageEncoder;
import com.ezweb.engine.util.RemotingUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * @author : zuodp
 * @version : 1.10
 */
public class NettyServer {
	private static Logger logger = LoggerFactory.getLogger(NettyServer.class);

	private EventLoopGroup bossGroup;
	private EventLoopGroup workerGroup;
	private DefaultEventExecutorGroup defaultEventExecutorGroup;
	private ChannelFuture bindFuture;
	private ServerHandlerCreator<? extends AbsServerHandler> serverHandlerCreator = null;
	private NettyConnectManager connectManager = new NettyConnectManager();

	public NettyServer(ServerHandlerCreator<? extends AbsServerHandler> serverHandlerCreator) {
		this.serverHandlerCreator = serverHandlerCreator;
	}

	public void serve(int port) {
		logger.info("Netty Server is starting");

		ServerBootstrap b = configServer();

		try {
			// start server
			bindFuture = b.bind(port).sync();

			// register shutown hook
			Runtime.getRuntime().addShutdownHook(new ShutdownThread());

		} catch (Exception e) {
			logger.error("Exception happen when start server", e);
		}
	}

	/**
	 * blocking to wait for close.
	 */
	public void waitForClose() throws InterruptedException {
		bindFuture.channel().closeFuture().sync();
	}

	public void stop() {
		logger.info("Netty server is stopping");

		bossGroup.shutdownGracefully();
		workerGroup.shutdownGracefully();
		defaultEventExecutorGroup.shutdownGracefully();

		logger.info("Netty server stoped");
	}

	private ServerBootstrap configServer() {
		if (RemotingUtil.isLinuxPlatform()) {
			bossGroup = new EpollEventLoopGroup(1, new DefaultThreadFactory("NettyBossGroup"));
			workerGroup = new EpollEventLoopGroup(3, new DefaultThreadFactory("NettyWorkerGroup"));
		} else {
			bossGroup = new NioEventLoopGroup(1, new DefaultThreadFactory("NettyBossGroup"));
			workerGroup = new NioEventLoopGroup(3, new DefaultThreadFactory("NettyWorkerGroup"));
		}
		defaultEventExecutorGroup = new DefaultEventExecutorGroup(8, new DefaultThreadFactory("NettyBizGroup"));

		ServerBootstrap b = new ServerBootstrap();
		b.group(bossGroup, workerGroup)
				.channel(RemotingUtil.isLinuxPlatform() ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
				.option(ChannelOption.SO_BACKLOG, 1024)
				.option(ChannelOption.SO_REUSEADDR, true)
				.childOption(ChannelOption.SO_KEEPALIVE, true)
				.childOption(ChannelOption.TCP_NODELAY, true)
				.childOption(ChannelOption.SO_SNDBUF, 65535)
				.childOption(ChannelOption.SO_RCVBUF, 65535)
				.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
				.childOption(ChannelOption.WRITE_BUFFER_WATER_MARK, WriteBufferWaterMark.DEFAULT);

		b.childHandler(new ChannelInitializer<SocketChannel>() {
			@Override
			public void initChannel(SocketChannel ch) throws Exception {
				ch.pipeline().addLast(
						defaultEventExecutorGroup,
						new LoggingHandler("com.ezweb.ServerBinLog", LogLevel.DEBUG),
						new CustTMessageDecoder(),
						new CustTMessageEncoder(),
						new IdleStateHandler(0, 0, 60, TimeUnit.SECONDS),
						new NettyConnectManageHandler(connectManager),
						serverHandlerCreator.create().setNettyConnectManager(connectManager)
				);
			}
		});

		return b;
	}

	class ShutdownThread extends Thread {
		@Override
		public void run() {
			NettyServer.this.stop();
		}
	}

	private class NettyConnectManageHandler extends ChannelDuplexHandler {
		private NettyConnectManager connectManager;

		public NettyConnectManageHandler(NettyConnectManager connectManager) {
			this.connectManager = connectManager;
		}

		@Override
		public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
			this.connectManager.registerChannel(ctx.channel());
			super.channelRegistered(ctx);
		}

		@Override
		public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
			this.connectManager.unRegisterChannel(ctx.channel());
			super.channelUnregistered(ctx);
		}

		@Override
		public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
			if (evt instanceof IdleStateEvent) {
				if (((IdleStateEvent) evt).state().equals(IdleState.ALL_IDLE)) {
					this.connectManager.closeIdleChannel(ctx.channel());
				}
			}

			super.userEventTriggered(ctx, evt);
		}

		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
			final String remoteAddress = RemotingUtil.parseChannelRemoteAddr(ctx.channel());
			logger.warn("NETTY SERVER PIPELINE: exceptionCaught {}, exception:", remoteAddress, cause);

			this.connectManager.closeIdleChannel(ctx.channel());
		}
	}
}
