package com.ezweb.engine;

import com.ezweb.demo.simple.Hello;
import com.ezweb.engine.exception.TBizException;
import com.ezweb.engine.rpc.RpcResponse;
import com.ezweb.engine.rpc.asm.ReflectUtils;
import com.ezweb.engine.rpc.serialize.Serialization;
import com.ezweb.engine.rpc.serialize.kryo.KryoSerializationImpl;
import com.ezweb.engine.util.ByteUtils;
import com.ezweb.engine.util.PureJavaCrc32;
import com.ezweb.engine.util.UnsignedUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.zip.CRC32;

/**
 * @author : zuodp
 * @version : 1.10
 */
public class TestBytes {
	@Test
	public void testAsm() throws Exception {
		Class<Hello> c = Hello.class;
		String desc = ReflectUtils.getRpcDesc(c.getMethod("say", List.class, Long.TYPE));
		Assert.assertEquals(desc, "(Ljava/util/List;J)");

		RpcResponse res = new RpcResponse();
		res.setException(new TBizException("xzzzzzzzzzz"));

		Serialization serialization = new KryoSerializationImpl();

		byte[] bytes = serialization.encodeNoType(res);
		RpcResponse v = serialization.decodeNoType(bytes, RpcResponse.class);

		System.out.println("v = " + v);
	}

	@Test
	public void testToBytes() {
		long f = System.currentTimeMillis();
		Random random = new Random(f);

		long a0 = random.nextLong();
		int a1 = random.nextInt();
		if (a1 > 0) a1 = -a1;

		byte[] buf0 = ByteUtils.toBytes(a0);
		byte[] buf1 = ByteUtils.toBytes(a1);

		long b0 = ByteUtils.toLong(buf0);
		Assert.assertTrue(a0 == b0);

		int b1 = ByteUtils.toInt(buf1);
		Assert.assertTrue(a1 == b1);

		byte[] xx = ByteUtils.toBytes(201711231528L);
		long vv1, vv2;
		{
			PureJavaCrc32 c = new PureJavaCrc32();
			c.update(xx, 0, xx.length);
			vv1 = c.getValue();
		}
		{
			CRC32 c = new CRC32();
			c.update(xx, 0, xx.length);
			vv2 = c.getValue();
		}
		Assert.assertEquals(vv1, vv2);
	}

	@Test
	public void testUIntBytes() {
		long uint32_a = 0xF1F1F1F1L; // 一定要L,表示是long
		byte[] a_bytes = UnsignedUtils.uint32ToBytes(uint32_a);
		long uint32_b = UnsignedUtils.toUInt32(a_bytes);
		Assert.assertTrue(uint32_a == uint32_b);

		int uint16_a = 0xF1F1;
		a_bytes = UnsignedUtils.uint16ToBytes(uint16_a);
		int uint16_b = UnsignedUtils.toUInt16(a_bytes);
		Assert.assertTrue(uint16_a == uint16_b);
	}

	@Test
	public void testWaitAsyncResult() throws ExecutionException, InterruptedException {
		CompletableFuture<Long> future = computeAsync();
		// 阻塞等待异步执行结果.
		future.whenComplete(new BiConsumer<Long, Throwable>() {
			@Override
			public void accept(Long R, Throwable E) {
				System.out.println("whenCompleteAsync() cur.thread_name = >>" + Thread.currentThread().getName());
				System.out.println("==> " + R);
			}
		});
	}

	@Test
	public void testAsyncWaitAsyncResult() throws ExecutionException, InterruptedException {
		CompletableFuture<Long> future = computeAsync();

		CallbackInvoker<Long, Throwable> callbackInvoker = new CallbackInvoker<Long, Throwable>() {
			@Override
			public void callbackResult(Long r, Throwable e) {
				System.out.println("callbackInvoker() cur.thread_name = >>" + Thread.currentThread().getName());
				System.out.println("==> " + r);
			}
		};
		// 非阻塞等待异步执行结果.
		future.whenCompleteAsync(new BiConsumer<Long, Throwable>() {
			@Override
			public void accept(Long R, Throwable E) {
				callbackInvoker.callbackResult(R, E);
			}
		}, create("worker_wait_thread"));
		TimeUnit.SECONDS.sleep(1);
	}

	private Executor create(String namefix) {
		return Executors.newFixedThreadPool(2, new ThreadFactory() {
			private AtomicInteger id = new AtomicInteger(1);

			@Override
			public Thread newThread(Runnable r) {
				return new Thread(r, namefix + "_" + id.getAndIncrement());
			}
		});
	}

	private Long compute() {
		System.out.println("compute() cur.thread_name = >>" + Thread.currentThread().getName());
		return System.currentTimeMillis();
	}

	private CompletableFuture<Long> computeAsync() {
		CompletableFuture<Long> future = CompletableFuture.supplyAsync(this::compute, create("compute_thread"));
		return future;
	}

	private interface CallbackInvoker<R, E> {
		void callbackResult(R r, E e);
	}
}
