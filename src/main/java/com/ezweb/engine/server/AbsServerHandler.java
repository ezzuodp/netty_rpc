package com.ezweb.engine.server;

import com.ezweb.engine.CustTMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author : zuodp
 * @version : 1.10
 */
public abstract class AbsServerHandler extends SimpleChannelInboundHandler<CustTMessage> {

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, CustTMessage request) throws Exception {
		CustTMessage response = handleCustTMessage(ctx, request);
		if (response != null) ctx.writeAndFlush(response);
	}

	protected CustTMessage handleCustTMessage(ChannelHandlerContext ctx, CustTMessage request) throws Exception {
		return null;
	}
}
