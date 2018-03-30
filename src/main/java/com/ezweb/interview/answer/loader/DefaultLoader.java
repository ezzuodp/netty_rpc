package com.ezweb.interview.answer.loader;

import com.ezweb.interview.answer.AnswerLoader;
import com.ezweb.interview.answer.Problem;
import com.ezweb.interview.answer.Result;
import com.ezweb.interview.answer.result.ResultImpl;

/**
 * 兜底答案加载器
 *
 * @author : zuodp
 * @version : 1.10
 */
class DefaultLoader implements AnswerLoader {

	@Override
	public Result load(Problem problem) {
		return new ResultImpl("兜底答案，不太标准！");
	}
}
