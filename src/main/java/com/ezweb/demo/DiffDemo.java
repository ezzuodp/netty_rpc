package com.ezweb.demo;

import com.google.common.collect.Lists;
import difflib.DiffUtils;
import difflib.Patch;

/**
 * @author : zuodp
 * @version : 1.10
 */
public class DiffDemo {
	public static void main(String[] args) {
		Patch<String> patch = DiffUtils.diff(
				Lists.newArrayList("GGATCGA".split("|")),
				Lists.newArrayList("GAATTCAGTTA".split("|"))
		);
		System.out.println("patch = " + patch);
	}
}
