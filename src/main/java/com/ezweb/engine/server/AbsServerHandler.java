package com.ezweb.engine.server;

import com.ezweb.engine.CustTMessage;
import com.ezweb.engine.CustTType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author : zuodp
 * @version : 1.10
 */
public abstract class AbsServerHandler extends SimpleChannelInboundHandler<CustTMessage> {

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, CustTMessage request) throws Exception {
		if (request.getType() == CustTType.HEARTBEAT) {
			handleHeartBeatCustTMessage(ctx, request);
			return;
		}
		CustTMessage response = handleCustTMessage(ctx, request);
		if (response != null) ctx.writeAndFlush(response);
	}

	protected void handleHeartBeatCustTMessage(ChannelHandlerContext ctx, CustTMessage request) {

	}

	protected abstract CustTMessage handleCustTMessage(ChannelHandlerContext ctx, CustTMessage request) throws Exception;
}
