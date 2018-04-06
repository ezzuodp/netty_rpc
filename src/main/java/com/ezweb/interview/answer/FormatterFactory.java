package com.ezweb.interview.answer;

/**
 * @author : zuodp
 * @version : 1.10
 */
public interface FormatterFactory {
	ResultFormatter getFormat(Terminal terminal);
}
