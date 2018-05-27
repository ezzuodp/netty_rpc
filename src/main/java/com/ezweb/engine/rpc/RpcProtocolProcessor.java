package com.ezweb.engine.rpc;

import com.ezweb.engine.CustTMessage;

/**
 * @author : zuodp
 * @version : 1.10
 */
public interface RpcProtocolProcessor {
	void addRpcProtocol(RpcProtocolCode rpcProtocol);

	void setRpcHandler(RpcHandler rpcHandler);

	CustTMessage doProcess(CustTMessage msg) throws Exception;

	void doProcessOneWay(CustTMessage msg) throws Exception;
}
