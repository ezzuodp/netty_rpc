package com.ezweb.engine.rpc.server;

import com.ezweb.engine.rpc.RpcProtocol;
import com.ezweb.engine.rpc.RpcRequest;
import com.ezweb.engine.rpc.RpcResponse;
import com.ezweb.engine.rpc.serialize.Decoder;
import com.ezweb.engine.rpc.serialize.Encoder;

import java.nio.ByteBuffer;

/**
 * @author : zuodp
 * @version : 1.10
 */
public class RpcProtocolImpl implements RpcProtocol {
	private Decoder decoder;
	private Encoder encoder;

	public RpcProtocolImpl(Decoder decoder, Encoder encoder) {
		this.decoder = decoder;
		this.encoder = encoder;
	}

	@Override
	public RpcRequest decodeReq(ByteBuffer msg) throws Exception {
		return (RpcRequest) this.decoder.decode(RpcRequest.class.getName(), msg);
	}

	@Override
	public RpcResponse decodeRes(ByteBuffer msg) throws Exception {
		return (RpcResponse) this.decoder.decode(RpcResponse.class.getName(), msg);
	}

	@Override
	public ByteBuffer encodeReq(RpcRequest orig) throws Exception {
		ByteBuffer byteBuf = ByteBuffer.allocate(256);
		this.encoder.encode(orig, byteBuf);
		byteBuf.flip(); // 作准备写出
		return byteBuf;
	}

	@Override
	public ByteBuffer encodeRes(RpcResponse orig) throws Exception {
		ByteBuffer byteBuf = ByteBuffer.allocate(256);
		this.encoder.encode(orig, byteBuf);
		byteBuf.flip(); // 作准备写出
		return byteBuf;
	}
}
