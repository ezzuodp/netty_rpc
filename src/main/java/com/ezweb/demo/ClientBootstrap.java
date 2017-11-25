package com.ezweb.demo;

import com.ezweb.engine.client.NettyClient;
import com.ezweb.engine.log.Log4j2System;
import com.ezweb.engine.rpc.RpcProtocol;
import com.ezweb.engine.rpc.client.AsyncRpcClient;
import com.ezweb.engine.rpc.serialize.kryo.KryoDecoder;
import com.ezweb.engine.rpc.serialize.kryo.KryoEncoder;
import com.ezweb.engine.rpc.server.RpcProtocolImpl;
import com.ezweb.demo.simple.HelloAsync;
import com.ezweb.demo.simple.TimeResult;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

/**
 * @author : zuodp
 * @version : 1.10
 */
public class ClientBootstrap {

	private static final Logger logger = LoggerFactory.getLogger(ClientBootstrap.class);

	public static void main(String[] args) throws Exception {
		new Log4j2System("client").init(null);

		ExecutorService async_pool = Executors.newFixedThreadPool(10, new DefaultThreadFactory("biz_async", true));
		// 同步写入并取回这个条结果.

		NettyClient socket_client = new NettyClient();
		socket_client.open("localhost", 9000);

	    /*{
			RpcClient rpcClient = new RpcClient();
			rpcClient.setProtocol(new RpcProtocolImpl());
			rpcClient.setNettyClient(client);
			Hello helloProxy = rpcClient.createRef(Hello.class);

			for (int i = 0; i < 1000; ++i) {
				TimeResult timeResult = helloProxy.say("interface say", System.currentTimeMillis());
				logger.info("timeResult.num = {}, {}", i, timeResult.getTime());
			}
		}*/
		{
			RpcProtocol protocol = new RpcProtocolImpl(new KryoDecoder(), new KryoEncoder());

			AsyncRpcClient rpcClient = new AsyncRpcClient();

			rpcClient.setProtocol(protocol);
			rpcClient.setNettyClient(socket_client);

			HelloAsync helloProxy = rpcClient.createRef(HelloAsync.class);

			int j = 0;
			for (int i = 0; i < 1000; ++i) {

				CompletableFuture<TimeResult> timeResultFuture = helloProxy.say("interface say", System.currentTimeMillis());
				++j;
				class ConsumerImpl implements BiConsumer<TimeResult, Throwable> {
					private final int _num;

					public ConsumerImpl(int _num) {
						this._num = _num;
					}

					@Override
					public void accept(TimeResult timeResult, Throwable throwable) {
						if (throwable != null) {
							// 这儿的throwable是CompletableException.
							logger.error("timeResultFuture.num = {}:{}", _num, throwable.getMessage());
						} else {
							// System.out.println(Thread.currentThread().getName());
							logger.info("timeResultFuture.num = {}, {}", _num, timeResult.getTime());
						}
					}
				}
				timeResultFuture.whenCompleteAsync(new ConsumerImpl(i), async_pool);
				if (j % 32 == 0) TimeUnit.MILLISECONDS.sleep(100L); // 并发32个.
			}
		}

		TimeUnit.MILLISECONDS.sleep(1000L);

		socket_client.close();
	}
}
