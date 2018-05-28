package com.ezweb.engine.rpc.server;

import com.ezweb.engine.CustTMessage;
import com.ezweb.engine.CustTType;
import com.ezweb.engine.rpc.RpcProtocolProcessor;
import com.ezweb.engine.server.AbsServerHandler;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author : zuodp
 * @version : 1.10
 */
public class RpcServerHandler extends AbsServerHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(RpcServerHandler.class);

	private RpcProtocolProcessor protocolProcessor = null;

	public RpcServerHandler(RpcProtocolProcessor protocolProcessor) {
		this.protocolProcessor = protocolProcessor;
	}

	@Override
	protected void handleHeartBeatCustTMessage(ChannelHandlerContext ctx, CustTMessage request) {
		LOGGER.debug("receive channel: <{}> heartbeat req:{}.", ctx.channel(), request);
	}

	@Override
	protected CustTMessage handleCustTMessage(ChannelHandlerContext ctx, CustTMessage request) throws Exception {
		CustTMessage response = null;
		if (request.getType() == CustTType.CALL) {
			// 这儿就可以另起 bizThread 进行 rpc 业务调用...
			response = protocolProcessor.doProcess(request);

			return response;
		}
		throw new IllegalArgumentException("不支持的消息：" + request.toString());
	}
}
