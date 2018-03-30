package com.ezweb.interview.answer.optcmd;

import com.ezweb.interview.answer.AnswerCommand;
import com.ezweb.interview.answer.Problem;

/**
 * @author : zuodp
 * @version : 1.10
 */
public class DefaultCommander implements AnswerCommand {
	@Override
	public void execute(Problem problem) {
		if (problem.inputText().startsWith("账户被冻结了怎么办")) {
			System.out.println("需验证用户账户是否处于冻结状态 ........");
		}
	}
}
