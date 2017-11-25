package com.ezweb.engine.rpc.simple;

import com.ezweb.engine.rpc.RpcResponse;

/**
 * @author : zuodp
 * @version : 1.10
 */
public class DefaultRpcResponse implements RpcResponse {
	private Object value;
	private Exception exception;

	@Override
	public Object getValue() {
		return value;
	}

	@Override
	public void setValue(Object value) {
		this.value = value;
	}

	@Override
	public Exception getException() {
		return exception;
	}

	@Override
	public void setException(Exception exception) {
		this.exception = exception;
	}
}
