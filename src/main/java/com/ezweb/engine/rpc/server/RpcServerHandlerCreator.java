package com.ezweb.engine.rpc.server;

import com.ezweb.engine.rpc.RpcHandler;
import com.ezweb.engine.rpc.RpcProtocol;
import com.ezweb.engine.server.ServerHandlerCreator;

/**
 * @author : zuodp
 * @version : 1.10
 */
public class RpcServerHandlerCreator implements ServerHandlerCreator<RpcServerHandler> {
	private RpcProtocolProcessorImpl rpcProtocolProcessor;

	public RpcServerHandlerCreator() {
		rpcProtocolProcessor = new RpcProtocolProcessorImpl();
	}

	public void addRpcHandler(Byte protoByte, RpcHandler rpcHandler) {
		rpcProtocolProcessor.addRpcHandler(protoByte, rpcHandler);
	}

	public void addRpcProtocol(Byte protoByte, RpcProtocol rpcProtocol) {
		rpcProtocolProcessor.addRpcProtocol(protoByte, rpcProtocol);
	}

	@Override
	public RpcServerHandler create() throws Exception {
		return new RpcServerHandler(this.rpcProtocolProcessor);
	}
}
