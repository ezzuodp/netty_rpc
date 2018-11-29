package com.ezweb.engine.exception;

/**
 * 服务端业务异常
 *
 * @author : zuodp
 * @version : 1.10
 */
public class TBizException extends RuntimeException {
	public TBizException() {
		super(null, null);
	}

	public TBizException(String message) {
		super(message, null);
	}

	public TBizException(String message, Throwable cause) {
		super(message, cause);
	}

	public TBizException(Throwable cause) {
		super(null, cause);
	}
}
