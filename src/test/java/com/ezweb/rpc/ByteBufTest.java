package com.ezweb.rpc;

import com.ezweb.engine.util.PooledAllocatorStats;
import com.google.common.collect.Maps;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
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
public class ByteBufTest {

	static {
		System.setProperty("io.netty.recycler.maxCapacityPerThread", "0");
		// 关闭线程局部缓存
//		System.setProperty("io.netty.allocator.tinyCacheSize", "0");
//		System.setProperty("io.netty.allocator.smallCacheSize", "0");
//		System.setProperty("io.netty.allocator.normalCacheSize", "0");

	}

	public static void main(String[] args) throws InterruptedException {
		Map<String, Object> beans = Maps.newHashMap();
		beans.put("com.ezweb.netty:name=pooledstats", new PooledAllocatorStats());

		MBeanExporter mBeanExporter = new MBeanExporter();
		mBeanExporter.setBeans(beans);
		mBeanExporter.afterPropertiesSet();
		mBeanExporter.afterSingletonsInstantiated();

		PooledByteBufAllocator alloc = PooledByteBufAllocator.DEFAULT;

		for (int j = 0; j < 100000; ++j) {
			ByteBuf buf = alloc.directBuffer(8192);
			buf.release();
			TimeUnit.MILLISECONDS.sleep(100L);
		}
//		Map<String, Object> ff = new PooledAllocatorStats().getMetrics();
//		for (String k : ff.keySet()) {
//			Object v = ff.get(k);
//			System.out.println(k + "=>" + v);
//		}
	}

}
