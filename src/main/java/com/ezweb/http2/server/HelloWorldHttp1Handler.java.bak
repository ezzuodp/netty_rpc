/*
 * Copyright 2014 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.ezweb.http2.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import static io.netty.buffer.Unpooled.copiedBuffer;
import static io.netty.buffer.Unpooled.unreleasableBuffer;
import static io.netty.handler.codec.http.HttpHeaderNames.*;
import static io.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * HTTP handler that responds with a "Hello World"
 */
public final class HelloWorldHttp1Handler extends SimpleChannelInboundHandler<FullHttpRequest> {
	static final ByteBuf HTTP_11_RESPONSE_BYTES = unreleasableBuffer(copiedBuffer(
			"<!DOCTYPE html>\n" +
					"<html>\n" +
					"<head><meta charset='utf-8' />\n" +
					"<title></title>\n" +
					"</head>\n" +
					"<body><h1>Direct. No Upgrade Attempted.</h1></body>\n" +
					"</html>\n",
			CharsetUtil.UTF_8
	));

	public HelloWorldHttp1Handler() {
	}

	@Override
	public void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception {
		if (HttpUtil.is100ContinueExpected(req)) {
			ctx.write(new DefaultFullHttpResponse(HTTP_1_1, CONTINUE));
		}
		boolean keepAlive = HttpUtil.isKeepAlive(req);

		ByteBuf content = ctx.alloc().buffer();
		content.writeBytes(HTTP_11_RESPONSE_BYTES.duplicate());

		FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, content);
		response.headers().set(CONTENT_TYPE, "text/html; charset=UTF-8");
		response.headers().setInt(CONTENT_LENGTH, response.content().readableBytes());

		if (!keepAlive) {
			ctx.write(response).addListener(ChannelFutureListener.CLOSE);
		} else {
			response.headers().set(CONNECTION, HttpHeaderValues.KEEP_ALIVE);
			ctx.write(response);
		}
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}
}
