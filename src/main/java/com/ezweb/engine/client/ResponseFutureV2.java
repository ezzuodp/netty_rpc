package com.ezweb.engine.client;

import com.ezweb.engine.CustTMessage;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import java.util.concurrent.locks.LockSupport;

/**
 * <一句话说明功能>
 * <功能详细描述>
 *
 * @author zuodengpeng
 * @version 1.0.0
 * @date 2018/6/8
 */
public class ResponseFutureV2 implements Future<CustTMessage> {
	private static AtomicReferenceFieldUpdater<ResponseFutureV2, FutureResult> innerResultOffset = null;
	private static AtomicReferenceFieldUpdater<ResponseFutureV2, WaitNode> waitersOffset = null;

	static {
		try {
			innerResultOffset = AtomicReferenceFieldUpdater.newUpdater(ResponseFutureV2.class, FutureResult.class, "innerResult");
			waitersOffset = AtomicReferenceFieldUpdater.newUpdater(ResponseFutureV2.class, WaitNode.class, "waiters");
		} catch (Exception ex) {
			throw new Error(ex);
		}
	}

	static final class WaitNode {
		volatile Thread thread;
		volatile WaitNode next;

		WaitNode() {
			thread = Thread.currentThread();
		}
	}

	private volatile FutureResult<CustTMessage> innerResult = null;
	private volatile WaitNode waiters;

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		boolean v = innerResultOffset.compareAndSet(this, null, new CancelFutureResult<CustTMessage>(mayInterruptIfRunning));
		try {
			if (mayInterruptIfRunning) {
				// TODO:想办法支持中断
			}
		} finally {
			finishCompletion();
		}
		return v;
	}

	@Override
	public boolean isCancelled() {
		return innerResult != null && innerResult instanceof CancelFutureResult;
	}

	@Override
	public boolean isDone() {
		return innerResult != null && innerResult instanceof DoneFutureResult;
	}

	@Override
	public CustTMessage get() {
		if (waiters == null)
			waiters = new WaitNode();
		else {
			WaitNode n = new WaitNode();
			n.next = this.waiters;
			this.waiters = n;
		}
		LockSupport.park();
		return isDone() ? ((DoneFutureResult<CustTMessage>) innerResult).getResult() : null;
	}

	@Override
	public CustTMessage get(long timeout, TimeUnit unit) {
		if (waiters == null)
			waiters = new WaitNode();
		else {
			WaitNode n = new WaitNode();
			n.next = this.waiters;
			this.waiters = n;
		}
		LockSupport.parkNanos(unit.toNanos(timeout));
		return isDone() ? ((DoneFutureResult<CustTMessage>) innerResult).getResult() : null;
	}

	protected boolean set(CustTMessage msg) {
		boolean v = innerResultOffset.compareAndSet(this, null, new DoneFutureResult<>(msg));
		return v;
	}

	protected void finishCompletion() {
		// assert state > COMPLETING;
		for (WaitNode q; (q = waitersOffset.get(this)) != null; ) {
			if (waitersOffset.compareAndSet(this, q, null)) {
				for (; ; ) {
					Thread t = q.thread;
					if (t != null) {
						q.thread = null;
						LockSupport.unpark(t);
					}
					WaitNode next = q.next;
					if (next == null)
						break;
					q.next = null; // unlink to help gc
					q = next;
				}
				break;
			}
		}
	}

	interface FutureResult<T> {
	}

	private static class CancelFutureResult<T> implements FutureResult<T> {
		boolean interrupt = false;

		public CancelFutureResult(boolean interrupt) {
			this.interrupt = interrupt;
		}
	}

	private static class DoneFutureResult<T> implements FutureResult<T> {
		T result;

		public DoneFutureResult(T result) {
			this.result = result;
		}

		public T getResult() {
			return result;
		}
	}

	private static Future<CustTMessage> doWork() {
		return new ResponseFutureV2();
	}

	public static void main(String[] args) throws ExecutionException, InterruptedException, TimeoutException {
		Future<CustTMessage> future = doWork();

		System.out.println(" wait for ......... ");
		CustTMessage r = future.get(5, TimeUnit.SECONDS);
		if (r != null) {
			System.out.println("r =>>>> " + r);
		} else {
			future.cancel(true);
		}
	}
}
