package com.ezweb.engine.client;

import com.ezweb.engine.CustTMessage;
import com.google.common.util.concurrent.AbstractFuture;

/**
 * 使用 guava.AbstractFuture实现的
 *
 * @author : zuodp
 * @version : 1.10
 */
@Deprecated
class ResponseFuture extends AbstractFuture<CustTMessage> {
	// 请求流水号
	private final int seqId;

	ResponseFuture(int seqId) {
		this.seqId = seqId;
	}

	public int getSeqId() {
		return seqId;
	}
}
