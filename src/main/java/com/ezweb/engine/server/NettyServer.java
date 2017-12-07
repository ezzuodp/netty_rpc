package com.ezweb.engine.server;

import com.ezweb.engine.NettyDecoder;
import com.ezweb.engine.NettyEncoder;
import com.ezweb.engine.util.RemotingUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
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
	private ChannelFuture f;
	private ServerHandlerCreator<? extends AbsServerHandler> serverHandlerCreator = null;

	public NettyServer(ServerHandlerCreator<? extends AbsServerHandler> serverHandlerCreator) {
		this.serverHandlerCreator = serverHandlerCreator;
	}

	public void serve(int port) {
		logger.info("Netty Server is starting");

		ServerBootstrap b = configServer();

		try {
			// start server
			f = b.bind(port).sync();

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
		f.channel().closeFuture().sync();
	}

	public void stop() {
		logger.info("Netty server is stopping");

		bossGroup.shutdownGracefully();
		workerGroup.shutdownGracefully();
		defaultEventExecutorGroup.shutdownGracefully();

		logger.info("Netty server stoped");
	}

	private ServerBootstrap configServer() {
		bossGroup = new NioEventLoopGroup(1, new DefaultThreadFactory("NettyBossGroup"));
		workerGroup = new NioEventLoopGroup(3, new DefaultThreadFactory("NettyWorkerSelectorGroup"));
		defaultEventExecutorGroup = new DefaultEventExecutorGroup(8, new DefaultThreadFactory("NettyExecGroup"));

		ServerBootstrap b = new ServerBootstrap();
		b.group(bossGroup, workerGroup)
				.channel(NioServerSocketChannel.class)
				.option(ChannelOption.SO_BACKLOG, 1024)
				.option(ChannelOption.SO_KEEPALIVE, true)
				.option(ChannelOption.SO_REUSEADDR, true)
				.childOption(ChannelOption.SO_KEEPALIVE, true)
				.childOption(ChannelOption.TCP_NODELAY, true)
				//.childOption(ChannelOption.SO_SNDBUF, nettyServerConfig.getServerSocketSndBufSize())
				//.childOption(ChannelOption.SO_RCVBUF, nettyServerConfig.getServerSocketRcvBufSize())
				.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);

		b.childHandler(new ChannelInitializer<SocketChannel>() {
			@Override
			public void initChannel(SocketChannel ch) throws Exception {
				ch.pipeline().addLast(
						defaultEventExecutorGroup,
						new LoggingHandler("com.ezweb.SERVLER", LogLevel.DEBUG),
						new NettyDecoder(),
						new NettyEncoder(),
						new IdleStateHandler(0, 0, 180, TimeUnit.SECONDS),
						new NettyConnectManageHandler(),
						serverHandlerCreator.create()
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

	class NettyConnectManageHandler extends ChannelDuplexHandler {
	        @Override
	        public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
	            final String remoteAddress = RemotingUtil.parseChannelRemoteAddr(ctx.channel());
		        logger.info("NETTY SERVER PIPELINE: channelRegistered {}", remoteAddress);
	            super.channelRegistered(ctx);
	        }

	        @Override
	        public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
	            final String remoteAddress = RemotingUtil.parseChannelRemoteAddr(ctx.channel());
		        logger.info("NETTY SERVER PIPELINE: channelUnregistered, the channel[{}]", remoteAddress);
	            super.channelUnregistered(ctx);
	        }

	        @Override
	        public void channelActive(ChannelHandlerContext ctx) throws Exception {
	            final String remoteAddress = RemotingUtil.parseChannelRemoteAddr(ctx.channel());
		        logger.info("NETTY SERVER PIPELINE: channelActive, the channel[{}]", remoteAddress);
	            super.channelActive(ctx);
	        }

	        @Override
	        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
	            final String remoteAddress = RemotingUtil.parseChannelRemoteAddr(ctx.channel());
		        logger.info("NETTY SERVER PIPELINE: channelInactive, the channel[{}]", remoteAddress);
	            super.channelInactive(ctx);
	        }

	        @Override
	        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
	            if (evt instanceof IdleStateEvent) {
	                IdleStateEvent event = (IdleStateEvent) evt;
	                if (event.state().equals(IdleState.ALL_IDLE)) {
	                    final String remoteAddress = RemotingUtil.parseChannelRemoteAddr(ctx.channel());
		                logger.warn("NETTY SERVER PIPELINE: IDLE exception [{}]", remoteAddress);
	                    RemotingUtil.closeChannel(ctx.channel());
	                }
	            }

	            ctx.fireUserEventTriggered(evt);
	        }

	        @Override
	        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
	            final String remoteAddress = RemotingUtil.parseChannelRemoteAddr(ctx.channel());
		        logger.warn("NETTY SERVER PIPELINE: exceptionCaught {}", remoteAddress);
		        logger.warn("NETTY SERVER PIPELINE: exceptionCaught exception.", cause);

		        RemotingUtil.closeChannel(ctx.channel());
	        }
	    }
}
