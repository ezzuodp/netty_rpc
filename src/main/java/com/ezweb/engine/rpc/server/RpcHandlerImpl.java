package com.ezweb.engine.rpc.server;

import com.ezweb.engine.exception.TBizException;
import com.ezweb.engine.rpc.simple.Invoker;
import com.ezweb.engine.rpc.RpcHandler;
import com.ezweb.engine.rpc.RpcRequest;
import com.ezweb.engine.rpc.RpcResponse;
import com.ezweb.engine.rpc.asm.ReflectUtils;
import com.ezweb.engine.rpc.asm.Wrapper;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentMap;

/**
 * @author : zuodp
 * @version : 1.10
 */
public class RpcHandlerImpl implements RpcHandler {
	private final static Logger Logger = LoggerFactory.getLogger(RpcHandlerImpl.class);
	private ConcurrentMap<String, Invoker<?>> invokerMap = Maps.newConcurrentMap();

	public RpcHandlerImpl() {
	}

	public <T> void addExport(Class<T> interfaceClass, T interfaceInstance) {
		Logger.info("导出:{} ...", interfaceClass.getName());

		Invoker<T> invoker = new SyncRpcHandlerImpl<T>(interfaceInstance, interfaceClass);
		invokerMap.putIfAbsent(interfaceClass.getName(), invoker);
	}

	@Override
	public RpcResponse doRequest(RpcRequest request) {
		return invokerMap.get(request.getInterfaceName()).invoke(request);
	}

	private static class SyncRpcHandlerImpl<T> implements Invoker<T> {
		private T ref = null;
		private Class<T> type = null;
		private Wrapper wrapper = null;

		SyncRpcHandlerImpl(T ref, Class<T> type) {
			this.ref = ref;
			this.type = type;
			this.wrapper = Wrapper.getWrapper(type);
		}

		@Override
		public Class<T> getInterface() {
			return type;
		}

		@Override
		public RpcResponse invoke(RpcRequest request) {
			RpcResponse rpcResponse = new RpcResponse();
			try {
				Class<?>[] argTypes = ReflectUtils.desc2classArray(request.getMethodDesc());
				Object val = wrapper.invokeMethod(this.ref, request.getMethodName(), argTypes, request.getArguments());
				rpcResponse.setValue(val);
			} catch (Exception e) {
				// e 是 InvocationTargetException
				rpcResponse.setException(new TBizException(e.getCause().getMessage()));
			}
			return rpcResponse;
		}
	}
}
