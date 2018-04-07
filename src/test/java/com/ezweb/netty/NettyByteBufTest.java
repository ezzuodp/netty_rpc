package com.ezweb.netty;

import com.ezweb.engine.log.Log4j2System;
import com.ezweb.engine.util.PooledAllocatorStats;
import com.google.common.collect.Maps;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.FastThreadLocal;
import io.netty.util.concurrent.FastThreadLocalThread;
import org.springframework.jmx.export.MBeanExporter;

import java.util.Map;
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
		// 关闭线程局部缓存[]
		/*
		System.setProperty("io.netty.allocator.tinyCacheSize", "0");
		System.setProperty("io.netty.allocator.smallCacheSize", "0");
		System.setProperty("io.netty.allocator.normalCacheSize", "0");
		*/
	}

	public static void main(String[] args) throws InterruptedException {
		new Log4j2System("server").init(null);

		Map<String, Object> beans = Maps.newHashMap();
		beans.put("com.ezweb.netty:name=pooledstats", new PooledAllocatorStats());

		MBeanExporter mBeanExporter = new MBeanExporter();
		mBeanExporter.setBeans(beans);
		mBeanExporter.afterPropertiesSet();
		mBeanExporter.afterSingletonsInstantiated();

		PooledByteBufAllocator alloc = PooledByteBufAllocator.DEFAULT;

		// 自己写的线程，必须手动调用[FastThreadLocal.removeAll();]才能放掉线程中的缓存分配字节数.
		// 如果是用 FastThreadLocalThread 就不用自己手动 FastThreadLocal.removeAll();
		FastThreadLocalThread t = new FastThreadLocalThread(new Runnable() {
			@Override
			public void run() {
				for (int j = 0; j < 100000; ++j) {
					ByteBuf buf = alloc.directBuffer(8192);
					buf.release();
				}
				System.out.println("alloc 100 * 1000 =>>>> ");
			}
		}, "TestAllocThread");

		t.setDaemon(true);
		t.start();

		// 关闭 局部线程池::: allocationsNormal = 100 * 1000, deallocationsNormal = 100 * 1000
		TimeUnit.SECONDS.sleep(10L);
		System.out.println("t = " + t);
	}
}
