package com.ezweb.engine.rpc.client;

import com.ezweb.engine.CustTMessage;
import com.ezweb.engine.exception.TSendRequestException;
import com.ezweb.engine.exception.TTimeoutException;
import com.ezweb.engine.rpc.RpcRequest;
import com.ezweb.engine.rpc.RpcResponse;
import com.ezweb.engine.rpc.asm.Proxy;
import com.ezweb.engine.rpc.simple.AsyncInvoker;
import com.ezweb.engine.rpc.simple.DefaultRpcResponse;

import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

/**
 * @author : zuodp
 * @version : 1.10
 */
public class AsyncRpcClient extends RpcClient{
	private ExecutorService async_pool = Executors.newWorkStealingPool(8);

	@Override
	public <T> T createRef(Class<T> importInterface) {
		// 生成一个client invoker.
		Proxy proxy = Proxy.getProxy(importInterface);

		AsyncInvoker<T> invoker = new AsyncInvokerClientImpl<>(importInterface);

		//noinspection unchecked
		return (T) proxy.newInstance(new AsyncInvocationHandlerImpl<>(async_pool, importInterface, invoker));
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
			ByteBuffer byteBuf;
			try {
				byteBuf = getProtocol().encodeReq(request);
			} catch (Exception e) {
				DefaultRpcResponse response = new DefaultRpcResponse();
				response.setException(e);
				return CompletableFuture.completedFuture(response);
			}

			CustTMessage req_msg = CustTMessage.newRequestMessage();
			req_msg.setBody(byteBuf);
			req_msg.setLen(byteBuf.limit());

			return CompletableFuture.supplyAsync(new RpcResponseSupplier(req_msg), async_pool);
		}
	}

	private class RpcResponseSupplier implements Supplier<RpcResponse> {
		private final CustTMessage req_msg;

		RpcResponseSupplier(CustTMessage req_msg) {
			this.req_msg = req_msg;
		}

		@Override
		public RpcResponse get() {
			RpcResponse response = null;
			try {
				CustTMessage resp_msg = getNettyClient().writeReq(req_msg, 10 * 1000);
				response = getProtocol().decodeRes(resp_msg.getBody());
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
