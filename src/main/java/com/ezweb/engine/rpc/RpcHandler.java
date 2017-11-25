package com.ezweb.engine.rpc;

/**
 * @author : zuodp
 * @version : 1.10
 */
public interface RpcHandler {

	RpcResponse doRequest(RpcRequest request);
}
