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

	private NettyConnectManager nettyConnectManager;

	protected NettyConnectManager getNettyConnectManager() {
		return nettyConnectManager;
	}

	protected AbsServerHandler setNettyConnectManager(NettyConnectManager nettyConnectManager) {
		this.nettyConnectManager = nettyConnectManager;
		return this;
	}

	@Override
	protected final void channelRead0(ChannelHandlerContext ctx, CustTMessage request) throws Exception {
		if (request.getType() == CustTType.HEARTBEAT) {
			handleHeartBeatCustTMessage(ctx, request);
		} else {
			handleCustTMessage(ctx, request);
		}
	}

	protected final void handleHeartBeatCustTMessage(ChannelHandlerContext ctx, CustTMessage request) {
		getNettyConnectManager().leaseRenewal(ctx.channel());
	}

	protected abstract void handleCustTMessage(ChannelHandlerContext ctx, CustTMessage request) throws Exception;
}
