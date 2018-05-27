package com.ezweb.engine.rpc.server;

import com.ezweb.engine.CustTMessage;
import com.ezweb.engine.CustTType;
import com.ezweb.engine.rpc.RpcProtocolProcessor;
import com.ezweb.engine.server.AbsServerHandler;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author : zuodp
 * @version : 1.10
 */
public class RpcServerHandler extends AbsServerHandler {
	private RpcProtocolProcessor protocolProcessor = null;

	public RpcServerHandler(RpcProtocolProcessor protocolProcessor) {
		this.protocolProcessor = protocolProcessor;
	}

	@Override
	public CustTMessage handleCustTMessage(ChannelHandlerContext ctx, CustTMessage request) throws Exception {
		CustTMessage response = null;
		if (request.getType() == CustTType.CALL) {
			// 这儿就可以另起 bizThread 进行 rpc 业务调用...
			response = protocolProcessor.doProcess(request);

			return response;

		} else if (request.getType() == CustTType.ONEWAY) {

			// 这儿就可以另起 bizThread 进行 rpc 业务调用...
			protocolProcessor.doProcessOneWay(request);

		}
		throw new IllegalArgumentException("不支持的消息：" + request.toString());
	}
}
