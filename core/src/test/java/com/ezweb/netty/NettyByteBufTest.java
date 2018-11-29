package com.ezweb.netty;

import com.ezweb.engine.log.Log4j2System;
import com.google.common.collect.Lists;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocatorMetric;
import io.netty.util.concurrent.FastThreadLocalThread;
import io.netty.util.internal.PlatformDependent;
import org.apache.logging.log4j.core.util.ReflectionUtil;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

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

		AtomicBoolean wait = new AtomicBoolean(false);
		AtomicInteger acout = new AtomicInteger(0); // malloc
		AtomicInteger bcout = new AtomicInteger(0); // free

		FastThreadLocalThread t = new FastThreadLocalThread(() -> {
			System.out.println("begin alloc --> " + getMemoryUsage());
			ByteBuf buf = alloc.directBuffer(128);
			acout.incrementAndGet();
			System.out.println("alloc.direct(128)  --> " + getMemoryUsage());
			long memAddr = buf.memoryAddress();

			for (int i = 0; i < 1024; ++i) {
				buf.writeByte(i);            // realloc 时产生 [free, realloc]
				if (buf.memoryAddress() != memAddr) {
					memAddr = buf.memoryAddress();
					bcout.incrementAndGet(); // free
					acout.incrementAndGet(); // realloc
				}
			}
			bufs.add(buf);

			System.out.println("after alloc 128, and write 1024 --> " + getMemoryUsage());
			wait.set(true);
		}, "TestAllocThread");
		t.start();

		System.out.println("alloc.count = " + acout.get());
		System.out.println(" free.count = " + bcout.get());


		while (!wait.get()) {
			TimeUnit.MILLISECONDS.sleep(500);
		}
		wait.set(false);

		System.out.println(ByteBufUtil.prettyHexDump(bufs.get(0)));

		FastThreadLocalThread t2 = new FastThreadLocalThread(() -> {
			for (int j = 0; j < bufs.size(); ++j) {
				ByteBuf buf = bufs.get(j);
				buf.release();
				bcout.incrementAndGet();
			}
			System.out.println("free 1024 =>>>> " + getMemoryUsage());
			wait.set(true);
		}, "TestFreeThread");
		t2.start();

		while (!wait.get()) {
			TimeUnit.MILLISECONDS.sleep(500);
		}
		int i = 0;
		PooledByteBufAllocatorMetric metric = alloc.metric();
		Assert.assertEquals(acout.get(), metric.directArenas().get(i).numAllocations());
		Assert.assertEquals(bcout.get(), metric.directArenas().get(i).numDeallocations());
	}

	public static long getMemoryUsage() {
		try {
			Field field = PlatformDependent.class.getDeclaredField("DIRECT_MEMORY_COUNTER");
			ReflectionUtil.makeAccessible(field);
			AtomicLong v = (AtomicLong) ReflectionUtil.getStaticFieldValue(field);
			return v.get();
		} catch (NoSuchFieldException e) {
			return -1L;
		}
	}
}
