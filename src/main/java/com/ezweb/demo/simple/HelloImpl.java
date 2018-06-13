package com.ezweb.demo.simple;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author : zuodp
 * @version : 1.10
 */
public class HelloImpl implements Hello {
	private Logger logger = LoggerFactory.getLogger(HelloImpl.class);

	@Override
	public TimeResult say(List<String> name, long curTime) {
		return aaaa();
	}

	private TimeResult aaaa() {
		return bbbb();
	}

	private TimeResult bbbb() {
		throw new IllegalArgumentException("打开数据库失败连接。。。。。。。。", null);
	}
}
