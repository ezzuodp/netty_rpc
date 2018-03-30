package com.ezweb.interview.answer.fmt;

import com.ezweb.interview.answer.Result;
import com.ezweb.interview.answer.ResultFormatter;

/**
 * @author : zuodp
 * @version : 1.10
 */
class H5Formater implements ResultFormatter {
	@Override
	public Result format(Result result) {
		return new H5Result(result.content());
	}

	private static class H5Result implements Result {
		private String origContent;

		public H5Result(String origContent) {
			this.origContent = origContent;
		}

		@Override
		public String content() {
			return "<H5>" + origContent + "</H5>";
		}
	}
}
