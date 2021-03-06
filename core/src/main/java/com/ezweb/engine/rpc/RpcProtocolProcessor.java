package com.ezweb.engine.rpc;

import com.ezweb.engine.CustTMessage;

/**
 * @author : zuodp
 * @version : 1.10
 */
public interface RpcProtocolProcessor {
	void addRpcProtocol(RpcProtocol rpcProtocol);

	void setRpcHandler(RpcHandler rpcHandler);

	CustTMessage doProcess(CustTMessage msg) throws Exception;
}
