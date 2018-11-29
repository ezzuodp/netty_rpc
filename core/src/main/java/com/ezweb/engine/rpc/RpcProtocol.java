package com.ezweb.engine.rpc;

import java.nio.ByteBuffer;


public interface RpcProtocol {

	byte codeType() ;

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