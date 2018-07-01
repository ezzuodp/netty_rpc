package com.ezweb.engine.server;

import com.ezweb.engine.util.RemotingUtil;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author : zuodp
 * @version : 1.10
 */
public class NettyConnectManager {
	// REGISTERED -> ACTIVE -> INACTIVE -> UNREGISTERED
	private static Logger logger = LoggerFactory.getLogger(NettyConnectManager.class);

	public void registerChannel(Channel channel) {
		final String remoteAddress = RemotingUtil.parseChannelRemoteAddr(channel);
		logger.info("NETTY SERVER PIPELINE: channelRegistered {}", remoteAddress);
	}

	public void unRegisterChannel(Channel channel) {
		final String remoteAddress = RemotingUtil.parseChannelRemoteAddr(channel);
		logger.info("NETTY SERVER PIPELINE: channelUnregistered, the channel[{}]", remoteAddress);
	}

	public void closeIdleChannel(Channel channel) {
		final String remoteAddress = RemotingUtil.parseChannelRemoteAddr(channel);
		logger.warn("NETTY SERVER PIPELINE: idle channel [{}]", remoteAddress);
		RemotingUtil.closeChannel(channel);
	}
}
