package com.ezweb.interview.answer.loader;

import com.ezweb.interview.answer.AnswerLoader;
import com.ezweb.interview.answer.Problem;
import com.ezweb.interview.answer.Result;

/**
 * @author : zuodp
 * @version : 1.10
 */
public class WrapperLoader implements AnswerLoader {
	private AnswerLoader db = new DatabaseLoader();
	private AnswerLoader net = new NetworkLoader();
	private AnswerLoader def = new DefaultLoader();

	public WrapperLoader() {
	}

	@Override
	public Result load(Problem problem) {
		// TODO: db, net 不分先后可以同时启动，加载成功后，打断另一个.
		Result result = db.load(problem);
		if (result == null) {
			result = net.load(problem);
		}
		// ------------------------------
		if (result == null) {
			result = def.load(problem);
		}
		return result;
	}
}
