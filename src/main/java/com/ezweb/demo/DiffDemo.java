package com.ezweb.demo;

import com.google.common.collect.Lists;
import difflib.DiffRow;
import difflib.DiffRowGenerator;
import difflib.DiffUtils;
import difflib.Patch;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : zuodp
 * @version : 1.10
 */
public class DiffDemo {
	public static void main(String[] args) {
		ArrayList<String> src = Lists.newArrayList("GGATCGA".split("|"));
		ArrayList<String> dst = Lists.newArrayList("GAATTCAGTTA".split("|"));
		Patch<String> patch = DiffUtils.diff(src, dst);
		List<DiffRow> diffRows = new DiffRowGenerator.Builder().showInlineDiffs(true).build().generateDiffRows(src, dst, patch);
		System.out.println(diffRows);
	}
}
