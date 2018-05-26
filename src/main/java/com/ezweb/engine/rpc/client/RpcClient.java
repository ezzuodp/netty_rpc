package com.ezweb.engine.rpc.client;

import com.ezweb.engine.CustTMessage;
import com.ezweb.engine.client.NettyClient;
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
		// 生成一个client invoker.
		Proxy proxy = Proxy.getProxy(importInterface);

		Invoker<T> invoker = new InvokerClientImpl<T>(importInterface, protocol, nettyClient);

		//noinspection unchecked
		return (T) proxy.newInstance(new InvocationHandlerImpl<T>(importInterface, invoker));
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
			try {
				ByteBuffer byteBuf = protocol.encodeRequest(request);
				CustTMessage req_msg = CustTMessage.newRequestMessage();
				req_msg.setBody(byteBuf);
				req_msg.setLen(byteBuf.limit());

				CustTMessage res_msg = this.client.writeReq(req_msg, 10 * 1000);
				RpcResponse response = protocol.decodeResponse(res_msg.getBody());
				return response;
			} catch (Exception e) {
				RpcResponse response = new RpcResponse();
				response.setException(e);
				return response;
			}
		}
	}

}
