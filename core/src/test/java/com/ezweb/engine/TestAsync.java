package com.ezweb.engine;

import com.google.common.util.concurrent.*;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.junit.Assert;

import java.util.concurrent.*;

public class TestAsync {
	private static ExecutorService th = Executors.newFixedThreadPool(3, new DefaultThreadFactory("async_w_"));
	private static ListeningExecutorService lstth = MoreExecutors.listeningDecorator(th);

	public static class A {
		public String get() throws Exception {
			System.out.println("_a = " + Thread.currentThread().getName());

			// 模拟花时长的操作
			TimeUnit.MILLISECONDS.sleep(10000L);

			// 返回值
			StringBuilder msg = new StringBuilder(128);
			msg.append("A{").append(System.currentTimeMillis()).append('-');
			msg.append(System.currentTimeMillis()).append("}");
			return msg.toString();
		}
	}

	public static class B {
		private A _a = new A();

		public ListenableFuture<String> asyncHello() {
			return lstth.submit(new Callable<String>() {
				@Override
				public String call() throws Exception {
					System.out.println("_b = " + Thread.currentThread().getName());
					return _a.get() + "|B{" + System.currentTimeMillis() + "}";
				}
			});
		}
	}

	public static void main(String[] args) {
		B _b = new B();
		ListenableFuture<String> f = _b.asyncHello();
		Futures.addCallback(f, new FutureCallback<String>() {
			@Override
			public void onSuccess(String result) {
				System.out.println("onSuccess(result := '" + result + "')");
			}

			@Override
			public void onFailure(Throwable t) {
				System.out.println("onFailure = >>>" + t.getMessage());
			}
		});
		try {
			String fv = f.get(1, TimeUnit.MILLISECONDS);
			System.out.println("fv = " + fv);
		} catch (TimeoutException e) {
			f.cancel(true);
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}

		//===========================================================================
		// JDK 8.0 形式
		CompletableFuture<String> cf = CompletableFuture.completedFuture("message").thenApplyAsync(s -> {
			System.out.println("_b = " + Thread.currentThread().getName());
			return s.toUpperCase();
		}, th);
		// 立即返回
		Assert.assertNull(cf.getNow(null));
		Assert.assertEquals("MESSAGE", cf.join());
		//===========================================================================

		th.shutdown();
	}
}
