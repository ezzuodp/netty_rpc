package com.ezweb.netty;

import com.ezweb.engine.log.Log4j2System;
import com.google.common.collect.Lists;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocatorMetric;
import io.netty.util.concurrent.FastThreadLocalThread;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <一句话说明功能>
 * <功能详细描述>
 *
 * @author zuodengpeng
 * @version 1.0.0
 * @date 2018/4/4
 */
public class NettyByteBufTest {

	static {
		// 关闭回收机制.
		System.setProperty("io.netty.recycler.maxCapacityPerThread", "0");

		// 关闭线程局部缓存数量
		System.setProperty("io.netty.allocator.tinyCacheSize", "0");
		System.setProperty("io.netty.allocator.smallCacheSize", "0");
		System.setProperty("io.netty.allocator.normalCacheSize", "0");
	}

	@Test
	public void testAlloc() throws InterruptedException {
		new Log4j2System("server").init(null);

		PooledByteBufAllocator alloc = PooledByteBufAllocator.DEFAULT;

		// 自己写的线程，必须手动调用[FastThreadLocal.removeAll();]才能放掉线程中的缓存分配字节数.
		// 如果是用 FastThreadLocalThread 就不用自己手动 FastThreadLocal.removeAll();
		List<ByteBuf> bufs = Lists.newArrayList();

		FastThreadLocalThread t = new FastThreadLocalThread(new Runnable() {
			@Override
			public void run() {
				for (int j = 0; j < 10; ++j) {
					ByteBuf buf = alloc.directBuffer(8192);
					//buf.release();
					bufs.add(buf);
				}
				System.out.println("alloc 10 * 8192 =>>>> ");
			}
		}, "TestAllocThread");

		t.setDaemon(true);
		t.start();

		FastThreadLocalThread t2 = new FastThreadLocalThread(new Runnable() {
			@Override
			public void run() {
				try {
					TimeUnit.SECONDS.sleep(3L);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				for (int j = 0; j < bufs.size(); ++j) {
					ByteBuf buf = bufs.get(j);
					buf.release();
				}
				System.out.println("free 10 * 8192 =>>>> ");
			}
		}, "TestFreeThread");

		t2.setDaemon(true);
		t2.start();

		// 关闭 局部线程池::: allocationsNormal = 100 * 1000, deallocationsNormal = 100 * 1000
		TimeUnit.SECONDS.sleep(3L);
		PooledByteBufAllocatorMetric metric = alloc.metric();

		int i = 0;
		Assert.assertEquals(10, metric.directArenas().get(i).numNormalAllocations());
		Assert.assertEquals(10, metric.directArenas().get(i).numNormalDeallocations()); // 关闭线程局部缓存后，这个内存回收线统计数才会是10.
	}
}
