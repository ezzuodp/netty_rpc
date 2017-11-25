package com.ezweb.engine.rpc.client;

import com.ezweb.engine.exception.TBizException;
import com.ezweb.engine.rpc.RpcResponse;
import com.ezweb.engine.rpc.asm.ReflectUtils;
import com.ezweb.engine.rpc.simple.AsyncInvoker;
import com.ezweb.engine.rpc.simple.DefaultRpcRequest;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

/**
 * @author : zuodp
 * @version : 1.10
 */
public class AsyncInvocationHandlerImpl<T> implements InvocationHandler {
	private static final String ASYNC = "Async";
	private final Class<T> clz;
	private final AsyncInvoker<T> rpcHandler;
	private final ExecutorService async_pool;

	AsyncInvocationHandlerImpl(ExecutorService async_pool, Class<T> clz, AsyncInvoker<T> rpcHandler) {
		this.async_pool = async_pool;
		this.clz = clz;
		this.rpcHandler = rpcHandler;
	}

	@Override
	public CompletableFuture<Object> invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if (isLocalMethod(clz, method)) {
			throw new IllegalAccessException("can not invoke local method:" + method.getName());
		}

		DefaultRpcRequest request = new DefaultRpcRequest();
		String interfaceName = clz.getName();
		// 处理掉 "Async".
		if (interfaceName.endsWith(ASYNC)) {
			request.setInterfaceName(interfaceName.substring(0, interfaceName.length() - ASYNC.length()));
		} else {
			request.setInterfaceName(clz.getName());
		}
		request.setMethodName(method.getName());
		request.setMethodDesc(ReflectUtils.getRpcDesc(method));
		request.setArguments(args);

		CompletableFuture<RpcResponse> response = this.rpcHandler.invoke(request);
		return response.thenApplyAsync(rpcResponse -> {
			if (rpcResponse.getException() != null)
				throw new TBizException(rpcResponse.getException().getMessage()); // 将biz异常转化一下.
			return rpcResponse.getValue();
		}, async_pool);
	}

	private boolean isLocalMethod(Class<T> clz, Method method) {
		if (method.getDeclaringClass().equals(Object.class)) {
			try {
				clz.getDeclaredMethod(method.getName(), method.getParameterTypes());
				return false;
			} catch (Exception e) {
				return true;
			}
		}
		return false;
	}
}
