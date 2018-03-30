package com.ezweb.interview.answer.loader;

import com.ezweb.interview.answer.AnswerLoader;
import com.ezweb.interview.answer.Problem;
import com.ezweb.interview.answer.Result;
import com.ezweb.interview.answer.result.ResultImpl;

/**
 * 网络答案加载器
 *
 * @author : zuodp
 * @version : 1.10
 */
class NetworkLoader implements AnswerLoader {
	@Override
	public Result load(Problem problem) {
		return new ResultImpl("从网络中得到的问题回答!!!");
	}
}
