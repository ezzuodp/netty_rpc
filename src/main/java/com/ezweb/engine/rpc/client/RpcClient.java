package com.ezweb.engine.rpc.client;

import com.ezweb.engine.CustTMessage;
import com.ezweb.engine.client.NettyClient;
import com.ezweb.engine.exception.TSendRequestException;
import com.ezweb.engine.exception.TSerializeException;
import com.ezweb.engine.exception.TTimeoutException;
import com.ezweb.engine.rpc.RpcProtocolCode;
import com.ezweb.engine.rpc.RpcRequest;
import com.ezweb.engine.rpc.RpcResponse;
import com.ezweb.engine.rpc.asm.Proxy;
import com.ezweb.engine.rpc.simple.Invoker;

import java.nio.ByteBuffer;

/**
 * @author : zuodp
 * @version : 1.10
 */
public class RpcClient {
	private NettyClient nettyClient;
	private RpcProtocolCode protocol;

	public void setNettyClient(NettyClient nettyClient) {
		this.nettyClient = nettyClient;
	}

	public void setProtocol(RpcProtocolCode protocol) {
		this.protocol = protocol;
	}

	public NettyClient getNettyClient() {
		return nettyClient;
	}

	public RpcProtocolCode getProtocol() {
		return protocol;
	}

	public <T> T createRef(Class<T> importInterface) {
		return this.createRef("/default", importInterface);
	}

	public <T> T createRef(String prefix, Class<T> importInterface) {
		// 生成一个client invoker.
		Proxy proxy = Proxy.getProxy(importInterface);

		Invoker<T> invoker = new InvokerClientImpl<T>(importInterface, protocol, nettyClient);

		//noinspection unchecked
		return (T) proxy.newInstance(new InvocationHandlerImpl<T>(prefix, importInterface, invoker));
	}

	private static class InvokerClientImpl<T> implements Invoker<T> {
		private Class<T> type = null;
		private RpcProtocolCode protocol = null;
		private NettyClient client = null;

		public InvokerClientImpl(Class<T> type, RpcProtocolCode protocol, NettyClient client) {
			this.type = type;
			this.protocol = protocol;
			this.client = client;
		}

		@Override
		public Class<T> getInterface() {
			return this.type;
		}

		@Override
		public RpcResponse invoke(RpcRequest request) {
			ByteBuffer reqBytes;
			try {
				reqBytes = protocol.encodeRequest(request);
			} catch (Exception e) {
				throw new TSerializeException(e.getMessage());
			}

			try {
				CustTMessage req_msg = CustTMessage.newRequestMessage();
				req_msg.setCodeType(protocol.codeType());
				req_msg.setBody(reqBytes);
				req_msg.setLen(reqBytes.limit());

				CustTMessage res_msg = this.client.writeReq(req_msg, 10 * 1000);
				RpcResponse response = protocol.decodeResponse(res_msg.getBody());
				return response;
				// 所有异常全部 throw.
			} catch (TSendRequestException | TTimeoutException | TSerializeException e) {
				throw e;
			} catch (Throwable t) {
				throw new TSendRequestException(t.getMessage());
			}
		}
	}

}
