package com.ezweb.interview.answer;

/**
 * @author : zuodp
 * @version : 1.10
 */
public interface Answer {
	/**
	 * 回应问题.
	 */
	Result ask(Terminal source, Problem problem);
}
