package com.ezweb.engine.rpc.server;

import com.ezweb.engine.CustTProtocolCode;
import com.ezweb.engine.rpc.RpcProtocol;
import com.ezweb.engine.rpc.RpcRequest;
import com.ezweb.engine.rpc.RpcResponse;
import com.ezweb.engine.rpc.serialize.Serialization;
import com.ezweb.engine.rpc.serialize.SerializationFactory;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @author : zuodp
 * @version : 1.10
 */
public class RpcProtocolImpl implements RpcProtocol {
	private Serialization serialization;

	public RpcProtocolImpl(SerializationFactory serializationFactory) {
		this.serialization = serializationFactory.newSerialization();
	}

	@Override
	public byte codeType() {
		return CustTProtocolCode.NORMAL;
	}

	@Override
	public RpcRequest decodeRequest(ByteBuffer msg) throws Exception {
		GZIPInputStream in = new GZIPInputStream(new ByteBufferInputStream(msg));
		// 解码
		CodedInputStream bytes = CodedInputStream.newInstance(in);

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
			rpcRequest.setArguments(args);
		}
		return rpcRequest;
	}

	@Override
	public ByteBuffer encodeRequest(RpcRequest orig) throws Exception {
		// 编码==>
		ByteBuffer buf = ByteBuffer.allocate(1024);
		GZIPOutputStream baos = new GZIPOutputStream(new ByteBufferOutputStream(buf));

		CodedOutputStream bytes = CodedOutputStream.newInstance(baos);
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
		baos.finish();
		baos.flush();

		buf.flip();
		return buf;
	}

	@Override
	public RpcResponse decodeResponse(ByteBuffer msg) throws Exception {
		GZIPInputStream in = new GZIPInputStream(new ByteBufferInputStream(msg));
		// 解码
		CodedInputStream bytes = CodedInputStream.newInstance(in);
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

		GZIPOutputStream baos = new GZIPOutputStream(new ByteBufferOutputStream(buf));

		CodedOutputStream bytes = CodedOutputStream.newInstance(baos);
		if (orig.getException() != null) {
			bytes.writeBoolNoTag(true);
			bytes.writeByteArrayNoTag(serialization.encode(orig.getException()));
		} else {
			bytes.writeBoolNoTag(false);
			bytes.writeByteArrayNoTag(serialization.encode(orig.getValue()));
		}
		bytes.flush();

		baos.finish();
		baos.flush();

		buf.flip();
		return buf;
	}

	private static class ByteBufferInputStream extends InputStream {
		private ByteBuffer buf = null;

		public ByteBufferInputStream(ByteBuffer buf) {
			this.buf = buf;
		}

		@Override
		public int read() throws IOException {
			return buf.get() & 0xFF;
		}

		@Override
		public int read(byte[] bytes, int offset, int length) throws IOException {
			if (length == 0) return 0;
			int count = Math.min(buf.remaining(), length);
			if (count == 0) return -1;
			buf.get(bytes, offset, count);
			return count;
		}

		@Override
		public int available() throws IOException {
			return buf.remaining();
		}
	}

	private static class ByteBufferOutputStream extends OutputStream {
		private ByteBuffer buf = null;

		public ByteBufferOutputStream(ByteBuffer buf) {
			this.buf = buf;
		}

		@Override
		public void write(int b) throws IOException {
			buf.put((byte) b);
		}

		@Override
		public void write(byte[] b, int off, int len) throws IOException {
			if (b == null) {
				throw new NullPointerException();
			} else if ((off < 0) || (off > b.length) || (len < 0) ||
					((off + len) > b.length) || ((off + len) < 0)) {
				throw new IndexOutOfBoundsException();
			} else if (len == 0) {
				return;
			}
			buf.put(b, off, len);
		}
	}
}
