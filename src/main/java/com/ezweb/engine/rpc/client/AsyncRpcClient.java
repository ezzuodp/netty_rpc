package com.ezweb.engine.rpc.client;

import com.ezweb.engine.rpc.RpcRequest;
import com.ezweb.engine.rpc.RpcResponse;
import com.ezweb.engine.rpc.asm.Proxy;
import com.ezweb.engine.rpc.simple.AsyncInvoker;
import com.ezweb.engine.rpc.simple.Invoker;
import com.ezweb.engine.rpc.simple.PrefixUtils;
import io.netty.util.concurrent.DefaultThreadFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author : zuodp
 * @version : 1.10
 */
public class AsyncRpcClient extends RpcClient {
	private ExecutorService async_pool = null;

	public AsyncRpcClient(int workerSize) {
		this.async_pool = Executors.newFixedThreadPool(workerSize, new DefaultThreadFactory("async_worker", true));
	}

	@Override
	public <T> T createRef(Class<T> importInterface) {
		return createRef(PrefixUtils.DEFAULT, importInterface);
	}

	@Override
	public <T> T createRef(String prefix, Class<T> importInterface) {
		// 生成一个client invoker.
		Proxy proxy = Proxy.getProxy(importInterface);

		//noinspection unchecked
		return (T) proxy.newInstance(new AsyncInvocationHandlerImpl<>(prefix, importInterface, new AsyncInvokerClientImpl<>(importInterface)));
	}

	private class AsyncInvokerClientImpl<T> implements AsyncInvoker<T> {
		private Invoker<T> delegate = null;

		AsyncInvokerClientImpl(Class<T> type) {
			this.delegate = new InvokerClientImpl<>(type);
		}

		@Override
		public Class<T> getInterface() {
			return this.delegate.getInterface();
		}

		@Override
		public CompletableFuture<RpcResponse> invoke(RpcRequest request) {
			return CompletableFuture.supplyAsync(() -> delegate.invoke(request), async_pool);
		}
	}
}
