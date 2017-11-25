package com.ezweb.engine.server;

import com.ezweb.engine.NettyDecoder;
import com.ezweb.engine.NettyEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
		workerGroup = new NioEventLoopGroup(3, new DefaultThreadFactory("NettyWorkerGroup"));
		defaultEventExecutorGroup = new DefaultEventExecutorGroup(8, new DefaultThreadFactory("NettyExecGroup"));

		ServerBootstrap b = new ServerBootstrap();
		b.group(bossGroup, workerGroup)
				.channel(NioServerSocketChannel.class)
				.option(ChannelOption.SO_BACKLOG, 1024).option(ChannelOption.TCP_NODELAY, true)
				.option(ChannelOption.SO_TIMEOUT, 6000)
				.childOption(ChannelOption.SO_REUSEADDR, true).childOption(ChannelOption.SO_KEEPALIVE, true)
				.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);

		b.childHandler(new ChannelInitializer<SocketChannel>() {
			@Override
			public void initChannel(SocketChannel ch) throws Exception {
				ch.pipeline().addLast(
						defaultEventExecutorGroup,
						new LoggingHandler("com.ezweb.SERVLER", LogLevel.DEBUG),
						new NettyDecoder(),
						new NettyEncoder(),
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
}
