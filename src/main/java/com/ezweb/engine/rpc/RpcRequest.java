package com.ezweb.engine.rpc;

/**
 * @author zuodengpeng
 * @date 2017-08-07
 * @since 1.0.0
 */
public interface RpcRequest {
    String getInterfaceName();

    void setInterfaceName(String interfaceName);

    String getMethodName();

    void setMethodName(String methodName);

    String getMethodDesc();

    void setMethodDesc(String methodDesc);

    Object[] getArguments();

    void setArguments(Object[] arguments);
}
