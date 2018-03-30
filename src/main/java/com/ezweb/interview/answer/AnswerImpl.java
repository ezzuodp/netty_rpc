package com.ezweb.interview.answer;

import com.ezweb.interview.answer.fmt.FormatterFactory;
import com.ezweb.interview.answer.loader.WrapperLoader;
import com.ezweb.interview.answer.optcmd.DefaultCommander;

import java.util.concurrent.Callable;

/**
 * @author : zuodp
 * @version : 1.10
 */
public class AnswerImpl implements Answer {
	@Override
	public Result ask(Terminal source, Problem problem) {
		return new LoaderWorker(source, problem).call();
	}

	private class LoaderWorker implements Callable<Result> {
		private FormatterFactory fmtFactory = new FormatterFactory();
		private final Terminal source;
		private final Problem problem;

		public LoaderWorker(Terminal source, Problem problem) {
			this.source = source;
			this.problem = problem;
		}

		@Override
		public Result call() {
			// 处理应答节点还需支持一些额外操作
			new DefaultCommander().execute(problem);
			// 加载问题
			Result result = new WrapperLoader().load(this.problem);
			// 返回终端结果
			ResultFormatter fmt = fmtFactory.getFormat(source);
			Result fmtResult = fmt.format(result);
			// 补充结果.
			return fmtFactory.getAppendFormatter().format(fmtResult);
		}
	}
}
