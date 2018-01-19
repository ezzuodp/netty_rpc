package com.ezweb.demo;

import org.openjdk.jol.info.ClassData;
import org.openjdk.jol.layouters.CurrentLayouter;

/**
 * <一句话说明功能>
 * <功能详细描述>
 *
 * @author zuodengpeng
 * @version 1.0.0
 * @date 2018/1/19
 */
public class JolDemo {
	static class Item {

	}

	public static void main(String[] args) {
		ClassData classData = ClassData.parseInstance(new Item());
		CurrentLayouter currentLayouter = new CurrentLayouter();
		System.out.println(currentLayouter.layout(classData).toPrintable());
	}
}
