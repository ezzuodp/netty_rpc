package com.ezweb.engine.rpc;

import java.nio.ByteBuffer;


public interface RpcProtocolCode {
	/**
	 * encode ByteBuffer to RPCObject
	 */
	RpcRequest decodeRequest(ByteBuffer msg) throws Exception;

	RpcResponse decodeResponse(ByteBuffer msg) throws Exception;

	/**
	 * encode RPCObject to ByteBuffer
	 */
	ByteBuffer encodeRequest(RpcRequest orig) throws Exception;

	ByteBuffer encodeResponse(RpcResponse orig) throws Exception;

}