package com.ezweb.demo.simple;

/**
 * @author : zuodp
 * @version : 1.10
 */
public class HelloImpl implements Hello {
	@Override
	public TimeResult say(String name, long curTime) {
		// System.out.println("name =>>> " + name);
		TimeResult r = new TimeResult(name, System.currentTimeMillis() - curTime);
		if (r.getTime() % 2 == 0) throw new RuntimeException("打开数据库失败");
		return r;
	}
}
