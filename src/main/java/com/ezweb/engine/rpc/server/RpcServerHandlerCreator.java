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

	public void addRpcHandler(RpcHandler rpcHandler) {
		rpcProtocolProcessor.setRpcHandler(rpcHandler);
	}

	public void addRpcProtocol(RpcProtocol rpcProtocol) {
		rpcProtocolProcessor.addRpcProtocol(rpcProtocol);
	}

	@Override
	public RpcServerHandler create() throws Exception {
		return new RpcServerHandler(this.rpcProtocolProcessor);
	}
}
