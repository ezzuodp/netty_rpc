package com.ezweb.engine.rpc.client;

import com.ezweb.engine.CustTMessage;
import com.ezweb.engine.client.NettyClient;
import com.ezweb.engine.exception.TSendRequestException;
import com.ezweb.engine.exception.TSerializeException;
import com.ezweb.engine.exception.TTimeoutException;
import com.ezweb.engine.rpc.RpcProtocol;
import com.ezweb.engine.rpc.RpcRequest;
import com.ezweb.engine.rpc.RpcResponse;
import com.ezweb.engine.rpc.asm.Proxy;
import com.ezweb.engine.rpc.simple.Invoker;
import com.ezweb.engine.rpc.simple.PrefixUtils;

import java.nio.ByteBuffer;

/**
 * @author : zuodp
 * @version : 1.10
 */
public class RpcClient {
	private NettyClient nettyClient;
	private RpcProtocol protocol;

	public void setNettyClient(NettyClient nettyClient) {
		this.nettyClient = nettyClient;
	}

	public void setProtocol(RpcProtocol protocol) {
		this.protocol = protocol;
	}

	public NettyClient getNettyClient() {
		return nettyClient;
	}

	public RpcProtocol getProtocol() {
		return protocol;
	}

	public <T> T createRef(Class<T> importInterface) {
		return this.createRef(PrefixUtils.DEFAULT, importInterface);
	}

	public <T> T createRef(String prefix, Class<T> importInterface) {
		// 生成一个client invoker.
		Proxy proxy = Proxy.getProxy(importInterface);

		//noinspection unchecked
		return (T) proxy.newInstance(new InvocationHandlerImpl<T>(prefix, importInterface, new InvokerClientImpl<T>(importInterface)));
	}

	protected class InvokerClientImpl<T> implements Invoker<T> {
		private Class<T> type = null;

		InvokerClientImpl(Class<T> type) {
			this.type = type;
		}

		@Override
		public Class<T> getInterface() {
			return this.type;
		}

		@Override
		public RpcResponse invoke(RpcRequest request) {
			ByteBuffer reqBytes;
			try {
				reqBytes = getProtocol().encodeRequest(request);
			} catch (Exception e) {
				throw new TSerializeException("encode rpc request exception!", e);
			}

			CustTMessage req_msg = CustTMessage.newRequestMessage();
			req_msg.setCodeType(getProtocol().codeType());
			req_msg.setBody(reqBytes);
			req_msg.setLen(reqBytes.limit());

			CustTMessage resp_msg = null;
			try {
				resp_msg = getNettyClient().writeReq(req_msg, 10 * 1000);
				// 所有异常全部 throw.
			} catch (TSendRequestException | TTimeoutException e) {
				throw e;
			} catch (Exception t) {
				throw new TSendRequestException("write rpc request exception!", t);
			}

			try {
				return getProtocol().decodeResponse(resp_msg.getBody());
			} catch (Exception t) {
				throw new TSerializeException("decode rpc response exception!", t);
			}
		}
	}

}
