package com.ezweb.interview.answer.fmt;

import com.ezweb.interview.answer.Result;
import com.ezweb.interview.answer.ResultFormatter;

/**
 * @author : zuodp
 * @version : 1.10
 */
class WebFormatter implements ResultFormatter {
	@Override
	public Result format(Result result) {
		return new HtmlResult(result.content());
	}

	private static class HtmlResult implements Result {
		private String htmlFormat = "<html>%s</html>";
		private String origContent;

		public HtmlResult(String origContent) {
			this.origContent = origContent;
		}

		@Override
		public String content() {
			return String.format(htmlFormat, origContent);
		}
	}
}
