package com.ezweb.engine.rpc.simple;

import com.ezweb.engine.rpc.RpcRequest;
import com.ezweb.engine.rpc.RpcResponse;

/**
 * @author : zuodp
 * @version : 1.10
 */
public interface Invoker<T> {

	Class<T> getInterface();

	RpcResponse invoke(RpcRequest request);
}
