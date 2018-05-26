package com.ezweb.engine.rpc.server;

import com.ezweb.engine.rpc.RpcProtocolCode;
import com.ezweb.engine.rpc.RpcRequest;
import com.ezweb.engine.rpc.RpcResponse;
import com.ezweb.engine.rpc.serialize.Serialization;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;

import java.nio.ByteBuffer;

/**
 * @author : zuodp
 * @version : 1.10
 */
public class RpcProtocolCodeImpl implements RpcProtocolCode {
	private Serialization serialization;

	public RpcProtocolCodeImpl(Serialization serialization) {
		this.serialization = serialization;
	}

	@Override
	public RpcRequest decodeRequest(ByteBuffer msg) throws Exception {
		// 解码
		CodedInputStream bytes = CodedInputStream.newInstance(msg);

		RpcRequest rpcRequest = new RpcRequest();

		rpcRequest.setInterfaceName(bytes.readString());
		rpcRequest.setMethodName(bytes.readString());
		rpcRequest.setMethodDesc(bytes.readString());
		int argLen = bytes.readInt32();

		if (argLen > 0) {
			Object[] args = new Object[argLen];
			for (int i = 0; i < argLen; ++i) {
				args[i] = this.serialization.decode(bytes.readByteArray());
			}
		}
		return rpcRequest;
	}

	@Override
	public ByteBuffer encodeRequest(RpcRequest orig) throws Exception {
		// 编码==>
		ByteBuffer buf = ByteBuffer.allocate(1024);

		CodedOutputStream bytes = CodedOutputStream.newInstance(buf);
		bytes.writeStringNoTag(orig.getInterfaceName());
		bytes.writeStringNoTag(orig.getMethodName());
		bytes.writeStringNoTag(orig.getMethodDesc());

		Object[] arguments = orig.getArguments();
		int len = arguments == null ? 0 : arguments.length;
		bytes.writeInt32NoTag(len);

		for (int i = 0; i < len; ++i) {
			bytes.writeByteArrayNoTag(serialization.encode(arguments[i]));
		}
		bytes.flush();

		buf.flip();
		return buf;
	}

	@Override
	public RpcResponse decodeResponse(ByteBuffer msg) throws Exception {
		// 解码
		CodedInputStream bytes = CodedInputStream.newInstance(msg);
		RpcResponse rpcResponse = new RpcResponse();

		boolean haveException = bytes.readBool();
		if (haveException) {
			rpcResponse.setException(serialization.decode(bytes.readByteArray()));
		} else {
			rpcResponse.setValue(serialization.decode(bytes.readByteArray()));
		}
		return rpcResponse;
	}

	@Override
	public ByteBuffer encodeResponse(RpcResponse orig) throws Exception {
		// 编码==>
		ByteBuffer buf = ByteBuffer.allocate(1024);

		CodedOutputStream bytes = CodedOutputStream.newInstance(buf);
		if (orig.getException() != null) {
			bytes.writeBoolNoTag(true);
			bytes.writeByteArrayNoTag(serialization.encode(orig.getException()));
		} else {
			bytes.writeBoolNoTag(false);
			bytes.writeByteArrayNoTag(serialization.encode(orig.getValue()));
		}
		bytes.flush();

		buf.flip();
		return buf;
	}
}
