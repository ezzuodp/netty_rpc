package com.ezweb.engine.rpc.client;

import com.ezweb.engine.rpc.RpcRequest;
import com.ezweb.engine.rpc.RpcResponse;
import com.ezweb.engine.rpc.asm.ReflectUtils;
import com.ezweb.engine.rpc.simple.Invoker;
import com.ezweb.engine.rpc.simple.PrefixUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author : zuodp
 * @version : 1.10
 */
public class InvocationHandlerImpl<T> implements InvocationHandler {
	private final String prefix;
	private final Class<T> clz;
	private final Invoker<T> rpcHandler;

	InvocationHandlerImpl(String prefix, Class<T> clz, Invoker<T> rpcHandler) {
		this.prefix = prefix;
		this.clz = clz;
		this.rpcHandler = rpcHandler;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if (HandlerUtils.isLocalMethod(clz, method)) {
			throw new IllegalAccessException("can not invoke local method:" + method.getName());
		}

		RpcRequest request = new RpcRequest();
		request.setInterfaceName(PrefixUtils.buildRefUrl(prefix, clz.getName()));
		request.setMethodName(method.getName());
		request.setMethodDesc(ReflectUtils.getRpcDesc(method));
		request.setArguments(args);

		RpcResponse response = this.rpcHandler.invoke(request);
		// 如果有异常
		if (response.getException() != null) throw response.getException();
		return response.getValue();
	}
}
