package com.ezweb.engine.rpc.client;

import com.ezweb.engine.CustTMessage;
import com.ezweb.engine.exception.TSendRequestException;
import com.ezweb.engine.exception.TTimeoutException;
import com.ezweb.engine.rpc.RpcRequest;
import com.ezweb.engine.rpc.RpcResponse;
import com.ezweb.engine.rpc.asm.Proxy;
import com.ezweb.engine.rpc.simple.AsyncInvoker;
import io.netty.util.concurrent.DefaultThreadFactory;

import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

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
		// 生成一个client invoker.
		Proxy proxy = Proxy.getProxy(importInterface);

		AsyncInvoker<T> invoker = new AsyncInvokerClientImpl<>(importInterface);

		//noinspection unchecked
		return (T) proxy.newInstance(new AsyncInvocationHandlerImpl<>(importInterface, invoker));
	}

	private class AsyncInvokerClientImpl<T> implements AsyncInvoker<T> {
		private Class<T> type = null;

		AsyncInvokerClientImpl(Class<T> type) {
			this.type = type;
		}

		@Override
		public Class<T> getInterface() {
			return this.type;
		}

		@Override
		public CompletableFuture<RpcResponse> invoke(RpcRequest request) {
			return CompletableFuture.supplyAsync(new RpcResponseSupplier(request), async_pool);
		}
	}

	private class RpcResponseSupplier implements Supplier<RpcResponse> {
		private final RpcRequest request;

		public RpcResponseSupplier(RpcRequest request) {
			this.request = request;
		}

		@Override
		public RpcResponse get() {
			ByteBuffer reqBytes;
			try {
				reqBytes = getProtocol().encodeRequest(this.request);
			} catch (Exception e) {
				throw new TSendRequestException(e.getMessage());
			}

			CustTMessage req_msg = CustTMessage.newRequestMessage();
			req_msg.setCodeType(getProtocol().codeType());
			req_msg.setBody(reqBytes);
			req_msg.setLen(reqBytes.limit());

			RpcResponse response = null;
			try {
				CustTMessage resp_msg = getNettyClient().writeReq(req_msg, 10 * 1000);
				response = getProtocol().decodeResponse(resp_msg.getBody());
				return response;
				// 所有异常全部 throw.
			} catch (TSendRequestException | TTimeoutException e) {
				throw e;
			} catch (Throwable t) {
				throw new TSendRequestException(t.getMessage());
			}
		}
	}

}
