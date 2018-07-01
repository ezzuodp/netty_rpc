package com.ezweb.engine.server;

import com.ezweb.engine.CustTMessage;
import com.ezweb.engine.util.RemotingUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentSkipListSet;

/**
 * REGISTERED -> { ACTIVE -> INACTIVE } -> UNREGISTERED
 *
 * @author : zuodp
 * @version : 1.10
 */
public class NettyConnectManager {

	private static Logger LOGGER = LoggerFactory.getLogger(NettyConnectManager.class);

	private ConcurrentSkipListSet<Channel> allChannel = new ConcurrentSkipListSet<>();


	public void registerChannel(Channel channel) {
		LOGGER.info("NETTY SERVER PIPELINE: channel: {} Registered.", channel);
		allChannel.add(channel);
	}

	public void leaseRenewal(Channel channel) {
		LOGGER.info("NETTY SERVER PIPELINE: channel: {} Lease Renewal.", channel);
		if (allChannel.contains(channel)) {

		}
	}

	public void unRegisterChannel(Channel channel) {
		LOGGER.info("NETTY SERVER PIPELINE: channel: {} Unregistered.", channel);
		allChannel.remove(channel);
	}

	public void closeIdleChannel(Channel channel) {
		LOGGER.info("NETTY SERVER PIPELINE: channel: {} Close idle .", channel);
		allChannel.remove(channel);
		RemotingUtil.closeChannel(channel);
	}

	public CustTMessage handleAdminCommand(ChannelHandlerContext ctx, CustTMessage request) {
		return null;
	}
}