package com.ezweb.engine.rpc.server;

import com.ezweb.engine.CustTMessage;
import com.ezweb.engine.exception.TBizException;
import com.ezweb.engine.exception.TSerializeException;
import com.ezweb.engine.rpc.*;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentMap;

/**
 * @author : zuodp
 * @version : 1.10
 */
public class RpcProtocolProcessorImpl implements RpcProtocolProcessor {
	private static final Logger LOG = LoggerFactory.getLogger(RpcProtocolProcessorImpl.class);

	private ConcurrentMap<Byte, RpcProtocol> rpcProtocols = Maps.newConcurrentMap();
	private RpcHandler rpcHandler = null;

	public RpcProtocolProcessorImpl() {
	}

	@Override
	public void addRpcProtocol(RpcProtocol rpcProtocol) {
		rpcProtocols.put(rpcProtocol.codeType(), rpcProtocol);
	}

	@Override
	public void setRpcHandler(RpcHandler rpcHandler) {
		this.rpcHandler = rpcHandler;
	}

	protected RpcHandler getRpcHandler() {
		return rpcHandler;
	}

	protected RpcProtocol getRpcProtocol(Byte protoType) {
		return rpcProtocols.get(protoType);
	}

	@Override
	public CustTMessage doProcess(CustTMessage reqmsg) throws Exception {
		RpcProtocol rpcCodeProtocol = getRpcProtocol(reqmsg.getCodeType());

		RpcRequest req = null;
		RpcResponse res = null;
		try {
			req = rpcCodeProtocol.decodeRequest(reqmsg.getBody());
		} catch (Exception e) {
			LOG.error("解码 {} 出错：", reqmsg, e);
			res = new RpcResponse();
			res.setException(new TSerializeException(e.getMessage()));
		}

		try {
			// 执行req调用.
			res = getRpcHandler().doRequest(req);
		} catch (Exception e) {
			LOG.error("执行业务调用 {} 出错：", reqmsg, e);
			res = new RpcResponse();
			res.setException(new TBizException(e.getMessage()));
		}

		ByteBuffer byteBuf = rpcCodeProtocol.encodeResponse(res);

		CustTMessage resmsg = CustTMessage.newResponseMessage();
		resmsg.setCodeType(rpcCodeProtocol.codeType());
		resmsg.setSeqId(reqmsg.getSeqId());
		resmsg.setLen(byteBuf.limit());
		resmsg.setBody(byteBuf);
		return resmsg;
	}
}
