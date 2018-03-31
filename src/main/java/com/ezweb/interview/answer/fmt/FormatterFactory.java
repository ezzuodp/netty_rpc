package com.ezweb.interview.answer.fmt;

import com.ezweb.interview.answer.Result;
import com.ezweb.interview.answer.ResultFormatter;
import com.ezweb.interview.answer.Terminal;

/**
 * @author : zuodp
 * @version : 1.10
 */
public class FormatterFactory {
	private ResultFormatter h5Formatter = new H5Formater();
	private ResultFormatter webFormatter = new WebFormatter();

	public ResultFormatter getFormat(Terminal terminal) {
		if (terminal.type() == Terminal.H5) {
			return new FlagFormatter(h5Formatter);
		} else if (terminal.type() == Terminal.WEB) {
			return new FlagFormatter(webFormatter);
		} else {
			return new FlagFormatter(result -> result);
		}
	}

	private static class FlagFormatter implements ResultFormatter {
		private ResultFormatter formatter = null;

		public FlagFormatter(ResultFormatter formatter) {
			this.formatter = formatter;
		}

		@Override
		public Result format(Result result) {
			return new FlagResult(this.formatter.format(result));
		}
	}

	private static class FlagResult implements Result {
		private Result result;

		public FlagResult(Result result) {
			this.result = result;
		}

		@Override
		public String content() {
			return result.content() + "\"<BR>如果以上答案未解决您的问题，请拨打123456人工客服热线:)\"";
		}
	}
}
