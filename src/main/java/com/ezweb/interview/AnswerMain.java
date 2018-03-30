package com.ezweb.interview;

import com.ezweb.interview.answer.AnswerImpl;
import com.ezweb.interview.answer.Result;
import com.ezweb.interview.answer.problem.ProblemImpl;
import com.ezweb.interview.answer.terminal.H5Term;
import com.ezweb.interview.answer.terminal.WebTerm;

/**
 * @author : zuodp
 * @version : 1.10
 */
public class AnswerMain {
	public static void main(String[] args) {

		AnswerImpl answer = new AnswerImpl();
		System.out.println("=============================================>");
		{
			ProblemImpl problem = new ProblemImpl("账户被冻结了怎么办?");
			Result result = answer.ask(new H5Term(), problem);
			System.out.println("问：" + problem.inputText());
			System.out.println("答：" + result.content());
		}
		System.out.println("=============================================>");
		{
			ProblemImpl problem = new ProblemImpl("AAAAAAAAAa?");
			Result result = answer.ask(new WebTerm(), problem);
			System.out.println("问：" + problem.inputText());
			System.out.println("答：" + result.content());
		}
		System.out.println("=============================================>");
	}
}
