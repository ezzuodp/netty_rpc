package com.ezweb.interview.answer.loader;

import com.ezweb.interview.answer.AnswerLoader;
import com.ezweb.interview.answer.Problem;
import com.ezweb.interview.answer.Result;
import com.ezweb.interview.answer.result.ResultImpl;

/**
 * 数据库答案加载器
 *
 * @author : zuodp
 * @version : 1.10
 */
class DatabaseLoader implements AnswerLoader {
	@Override
	public Result load(Problem problem) {
		if (problem.inputText().startsWith("账户被冻结了怎么办"))
			return new ResultImpl("从数据库中得到的问题回答!!!");
		else
			return null;
	}
}
