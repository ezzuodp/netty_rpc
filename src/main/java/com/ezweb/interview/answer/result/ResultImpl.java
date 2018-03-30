package com.ezweb.interview.answer.result;

import com.ezweb.interview.answer.Result;

/**
 * @author : zuodp
 * @version : 1.10
 */
public class ResultImpl implements Result {
	private String content;

	public ResultImpl(String content) {
		this.content = content;
	}

	@Override
	public String content() {
		return this.content;
	}
}
