package com.ezweb.interview.answer.fmt;

import com.ezweb.interview.answer.Result;
import com.ezweb.interview.answer.ResultFormatter;
import com.ezweb.interview.answer.Terminal;
import com.ezweb.interview.answer.result.ResultImpl;

/**
 * @author : zuodp
 * @version : 1.10
 */
public class FormatterFactory {
	private ResultFormatter h5Formater = new H5Formater();
	private ResultFormatter webFormatter = new WebFormatter();
	private ResultFormatter appendFormatter = new ResultFormatter() {
		@Override
		public Result format(Result result) {
			return new ResultImpl(result.content() + "<BR>如果以上不落单，请打电话 220 :)");
		}
	};

	public ResultFormatter getFormat(Terminal terminal) {
		if (terminal.type() == Terminal.H5) {
			return h5Formater;
		} else if (terminal.type() == Terminal.WEB) {
			return webFormatter;
		} else {
			return result -> result;
		}
	}

	public ResultFormatter getAppendFormatter() {
		return appendFormatter;
	}
}
