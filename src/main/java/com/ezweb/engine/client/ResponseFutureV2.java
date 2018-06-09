package com.ezweb.engine.client;

import com.ezweb.engine.CustTMessage;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import java.util.concurrent.locks.LockSupport;

/**
 * 使用LockSupport实现的Future.
 *
 * @author zuodengpeng
 * @version 1.0.0
 * @date 2018/6/8
 */
class ResponseFutureV2 implements Future<CustTMessage> {
	private final static AtomicReferenceFieldUpdater<ResponseFutureV2, FutureResult> INNER_RESULT_UPDATER =
			AtomicReferenceFieldUpdater.newUpdater(ResponseFutureV2.class, FutureResult.class, "innerResult");

	private final static AtomicReferenceFieldUpdater<ResponseFutureV2, WaitNode> WAITERS_UPDATER =
			AtomicReferenceFieldUpdater.newUpdater(ResponseFutureV2.class, WaitNode.class, "waiters");

	static private final class WaitNode {
		volatile Thread thread;
		volatile WaitNode next;

		WaitNode() {
			thread = Thread.currentThread();
		}
	}

	private volatile FutureResult<CustTMessage> innerResult = null;
	private volatile WaitNode waiters;

	private final int seqId;

	ResponseFutureV2(int seqId) {
		this.seqId = seqId;
	}

	public int getSeqId() {
		return seqId;
	}

	public boolean set(CustTMessage msg) {
		boolean change = INNER_RESULT_UPDATER.compareAndSet(this, null, new DoneFutureResult<>(msg));
		if (!change) {
			return false;
		}
		this.finishCompletion();
		return true;
	}

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		boolean change = INNER_RESULT_UPDATER.compareAndSet(this, null, new CancelFutureResult<CustTMessage>(mayInterruptIfRunning));
		if (!change) {
			return false;
		}
		try {
			if (mayInterruptIfRunning) {
				// TODO:想办法支持中断执行线程
			}
		} finally {
			finishCompletion();
		}
		return true;
	}

	@Override
	public boolean isCancelled() {
		//noinspection unchecked
		FutureResult<CustTMessage> obj = INNER_RESULT_UPDATER.get(this);
		return (obj != null && obj instanceof CancelFutureResult);
	}

	@Override
	public boolean isDone() {
		//noinspection unchecked
		FutureResult<CustTMessage> obj = INNER_RESULT_UPDATER.get(this);
		return obj != null && obj instanceof DoneFutureResult;
	}

	@Override
	public CustTMessage get() throws InterruptedException, ExecutionException {
		if (Thread.interrupted()) {
			throw new InterruptedException();
		}
		//noinspection unchecked
		FutureResult<CustTMessage> obj = INNER_RESULT_UPDATER.get(this);
		if (obj != null) {
			return getDoneValue(obj);
		}
		return addAndWaitDone();
	}

	@Override
	public CustTMessage get(long timeout, TimeUnit unit) throws InterruptedException, TimeoutException, ExecutionException {
		if (Thread.interrupted()) {
			throw new InterruptedException();
		}
		//noinspection unchecked
		FutureResult<CustTMessage> obj = INNER_RESULT_UPDATER.get(this);
		if (obj != null) {
			return getDoneValue(obj);
		}
		return addAndWaitDone(unit.toNanos(timeout));
	}

	private CustTMessage addAndWaitDone() throws ExecutionException {
		WaitNode newNode = new WaitNode();
		boolean queued = false;
		do {
			queued = WAITERS_UPDATER.compareAndSet(this, newNode.next = waiters, newNode);
		} while (!queued);

		LockSupport.park(this);

		//noinspection unchecked
		return getDoneValue(INNER_RESULT_UPDATER.get(this));
	}

	private CustTMessage addAndWaitDone(long timeout) throws ExecutionException, TimeoutException {
		long endNanos = timeout + System.nanoTime();

		WaitNode newNode = new WaitNode();
		boolean queued = false;
		do {
			queued = WAITERS_UPDATER.compareAndSet(this, newNode.next = WAITERS_UPDATER.get(this), newNode);
		} while (!queued);

		long remainingNanos = endNanos - System.nanoTime();

		if (remainingNanos >= 1000L) {
			LockSupport.parkNanos(this, remainingNanos);
			//noinspection unchecked
			FutureResult<CustTMessage> obj = INNER_RESULT_UPDATER.get(this);
			if (obj != null) {
				return this.getDoneValue(obj);
			}
		} else {
			// 优化 < 1000L
			while (remainingNanos > 0L) {
				//noinspection unchecked
				FutureResult<CustTMessage> obj = INNER_RESULT_UPDATER.get(this);
				if (obj != null) {
					return this.getDoneValue(obj);
				}
				remainingNanos = endNanos - System.nanoTime();
			}
		}

		throw new TimeoutException();
	}

	private CustTMessage getDoneValue(FutureResult<CustTMessage> obj) throws ExecutionException {
		if (obj instanceof CancelFutureResult) {
			throw cancellationExceptionWithCause(((CancelFutureResult) obj).cause);
		} else if (obj instanceof DoneFutureResult) {
			return ((DoneFutureResult<CustTMessage>) obj).result;
		} else {
			return null;
		}
	}

	private CancellationException cancellationExceptionWithCause(Throwable cause) {
		CancellationException exception = new CancellationException("Task was cancelled.");
		exception.initCause(cause);
		return exception;
	}

	private void finishCompletion() {
		// assert state > COMPLETING;
		for (WaitNode q; (q = WAITERS_UPDATER.get(this)) != null; ) {
			if (WAITERS_UPDATER.compareAndSet(this, q, null)) {
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
		Exception cause = null;

		CancelFutureResult(boolean interrupt) {
			this.interrupt = interrupt;
			this.cause = new CancellationException("Future.cancel() was called.");
		}
	}

	private static class DoneFutureResult<T> implements FutureResult<T> {
		T result;

		DoneFutureResult(T result) {
			this.result = result;
		}
	}
}
