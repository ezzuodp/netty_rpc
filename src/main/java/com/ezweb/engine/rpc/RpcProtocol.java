package com.ezweb.engine.rpc;

import java.nio.ByteBuffer;


public interface RpcProtocol {

    /**
     * encode ByteBuffer to RPCObject
     */
    RpcRequest decodeReq(ByteBuffer msg) throws Exception;

    RpcResponse decodeRes(ByteBuffer msg) throws Exception;

    /**
     * encode RPCObject to ByteBuffer
     */
    ByteBuffer encodeReq(RpcRequest orig) throws Exception;

    ByteBuffer encodeRes(RpcResponse orig) throws Exception;

}