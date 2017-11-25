package com.ezweb.engine.rpc.server;

import com.ezweb.engine.CustTMessage;
import com.ezweb.engine.exception.TBizException;
import com.ezweb.engine.rpc.*;
import com.ezweb.engine.rpc.simple.DefaultRpcResponse;
import com.google.common.collect.Maps;

import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentMap;

/**
 * @author : zuodp
 * @version : 1.10
 */
public class RpcProtocolProcessorImpl implements RpcProtocolProcessor {
	private ConcurrentMap<Byte, RpcProtocol> rpcProtocols = Maps.newConcurrentMap();
	private ConcurrentMap<Byte, RpcHandler> rpcHandlers = Maps.newConcurrentMap();

	public RpcProtocolProcessorImpl() {
	}

	@Override
	public void addRpcProtocol(Byte protoType, RpcProtocol rpcProtocol) {
		rpcProtocols.put(protoType, rpcProtocol);
	}

	@Override
	public void addRpcHandler(Byte protoType, RpcHandler rpcHandler) {
		this.rpcHandlers.put(protoType, rpcHandler);
	}

	protected RpcHandler getRpcHandler(Byte protoType) {
		return rpcHandlers.get(protoType);
	}

	protected RpcProtocol getRpcProtocol(Byte protoType) {
		return rpcProtocols.get(protoType);
	}

	@Override
	public void doProcessOneWay(CustTMessage reqmsg) throws Exception {
		RpcProtocol rpcProtocol = getRpcProtocol(reqmsg.getProtoType());
		RpcHandler rpcHandler = getRpcHandler(reqmsg.getProtoType());

		RpcRequest req = rpcProtocol.decodeReq(reqmsg.getBody());
		rpcHandler.doRequest(req);
	}

	@Override
	public CustTMessage doProcess(CustTMessage reqmsg) throws Exception {
		RpcProtocol rpcProtocol = getRpcProtocol(reqmsg.getProtoType());
		RpcHandler rpcHandler = getRpcHandler(reqmsg.getProtoType());

		RpcRequest req = rpcProtocol.decodeReq(reqmsg.getBody());
		RpcResponse res = null;
		try {
			res = rpcHandler.doRequest(req);
		} catch (Exception e) {
			res = new DefaultRpcResponse();
			res.setException(new TBizException(e.getMessage()));
		}

		ByteBuffer byteBuf = rpcProtocol.encodeRes(res);

		CustTMessage resmsg = CustTMessage.newResponseMessage();
		resmsg.setSeqId(reqmsg.getSeqId());
		resmsg.setLen(byteBuf.limit());
		resmsg.setBody(byteBuf);
		return resmsg;
	}
}
