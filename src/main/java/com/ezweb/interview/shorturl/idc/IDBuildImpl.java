package com.ezweb.interview.shorturl.idc;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author : zuodp
 * @version : 1.10
 */
public class IDBuildImpl implements IDBuild {
	AtomicLong v = new AtomicLong(1000L);

	@Override
	public long buildId(String url) {
		// 生成一个小的，全局唯一的ID;
		return v.incrementAndGet();
	}
}
