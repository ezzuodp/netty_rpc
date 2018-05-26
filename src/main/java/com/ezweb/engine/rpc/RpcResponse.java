package com.ezweb.engine.rpc;

/**
 * @author zuodengpeng
 * @date 2017-08-07
 * @since 1.0.0
 */
public class RpcResponse {
	private Object value;
	private Exception exception;

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public Exception getException() {
		return exception;
	}

	public void setException(Exception exception) {
		this.exception = exception;
	}
}
