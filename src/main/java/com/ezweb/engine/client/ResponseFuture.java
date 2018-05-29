package com.ezweb.engine.client;

import com.ezweb.engine.CustTMessage;
import com.google.common.util.concurrent.AbstractFuture;

/**
 * @author : zuodp
 * @version : 1.10
 */
class ResponseFuture extends AbstractFuture<CustTMessage> {
	// 请求流水号
	private final int id;

	ResponseFuture(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	@Override
	public boolean set(CustTMessage value) {
		return super.set(value);
	}
}
