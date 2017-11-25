package com.ezweb.engine.rpc.simple;

import com.ezweb.engine.rpc.RpcRequest;
import com.ezweb.engine.rpc.RpcResponse;

import java.util.concurrent.CompletableFuture;

/**
 * @author : zuodp
 * @version : 1.10
 */
public interface AsyncInvoker<T> {

    Class<T> getInterface();

    CompletableFuture<RpcResponse> invoke(RpcRequest request);
}
