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
	private RpcProtocolProcessor protocolProcessor = null;

	public RpcServerHandler(RpcProtocolProcessor protocolProcessor) {
		this.protocolProcessor = protocolProcessor;
	}

	@Override
	protected void handleCustTMessage(ChannelHandlerContext ctx, CustTMessage request) throws Exception {
		if (request.getType() == CustTType.CALL) {
			// 这儿就可以另起 bizThread 进行 rpc 业务调用...
			CustTMessage response = protocolProcessor.doProcess(request);
			ctx.writeAndFlush(response);
		} else if (request.getType() == CustTType.ONEWAY) {
			handleOneWayCustTMessage(ctx, request);
		} else {
			throw new IllegalArgumentException("不支持的消息：" + request.toString());
		}
	}

	protected void handleOneWayCustTMessage(ChannelHandlerContext ctx, CustTMessage request) {

	}
}
