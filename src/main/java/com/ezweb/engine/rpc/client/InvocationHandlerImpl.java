package com.ezweb.engine.rpc.client;

import com.ezweb.engine.rpc.RpcResponse;
import com.ezweb.engine.rpc.asm.ReflectUtils;
import com.ezweb.engine.rpc.simple.DefaultRpcRequest;
import com.ezweb.engine.rpc.simple.Invoker;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author : zuodp
 * @version : 1.10
 */
public class InvocationHandlerImpl<T> implements InvocationHandler {
    private final Class<T> clz;
    private final Invoker<T> rpcHandler;

    InvocationHandlerImpl(Class<T> clz, Invoker<T> rpcHandler) {
        this.clz = clz;
        this.rpcHandler = rpcHandler;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (isLocalMethod(clz, method)) {
            throw new IllegalAccessException("can not invoke local method:" + method.getName());
        }

        DefaultRpcRequest request = new DefaultRpcRequest();
        request.setInterfaceName(clz.getName());
        request.setMethodName(method.getName());
        request.setMethodDesc(ReflectUtils.getRpcDesc(method));
        request.setArguments(args);

        RpcResponse response = this.rpcHandler.invoke(request);
        // 如果有异常
        if (response.getException() != null) throw response.getException();
        return response.getValue();
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
