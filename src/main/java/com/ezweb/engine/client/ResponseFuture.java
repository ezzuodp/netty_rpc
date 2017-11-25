package com.ezweb.engine.client;

import com.ezweb.engine.CustTMessage;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author : zuodp
 * @version : 1.10
 */
class ResponseFuture implements Future<CustTMessage> {
	private final CountDownLatch downLatch = new CountDownLatch(1);
	// 请求流水号
	private final int id;
	// 请求是否已经写入成功.
	private volatile boolean isOk;
	// 返回
	private volatile CustTMessage response;

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		return false;
	}

	@Override
	public boolean isCancelled() {
		return false;
	}

	@Override
	public boolean isDone() {
		return isOk;
	}

	@Override
	public CustTMessage get() throws InterruptedException, ExecutionException {
		return waitResponse();
	}

	@Override
	public CustTMessage get(long timeout, TimeUnit unit) throws InterruptedException {
		return waitResponse(timeout, unit);
	}

	public ResponseFuture(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setIsOk(boolean isOk) {
		this.isOk = isOk;
	}

	public boolean isOk() {
		return isOk;
	}

	public void setResponse(CustTMessage response) {
		this.response = response;
	}

	public CustTMessage waitResponse() throws InterruptedException {
		downLatch.await();
		return this.response;
	}

	public CustTMessage waitResponse(long timeOut, TimeUnit timeUnit) throws InterruptedException {
		downLatch.await(timeOut, timeUnit);
		return this.response;
	}

	public void release() {
		downLatch.countDown();
	}
}
