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
	protected final void channelRead0(ChannelHandlerContext ctx, CustTMessage request) throws Exception {
		handleCustTMessage(ctx, request);
	}

	protected abstract void handleCustTMessage(ChannelHandlerContext ctx, CustTMessage request) throws Exception;
}
