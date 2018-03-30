package com.ezweb.interview.answer.problem;

import com.ezweb.interview.answer.Problem;

/**
 * @author : zuodp
 * @version : 1.10
 */
public class ProblemImpl implements Problem {
	private String input;

	public ProblemImpl(String input) {
		this.input = input;
	}

	@Override
	public String inputText() {
		return this.input;
	}
}
