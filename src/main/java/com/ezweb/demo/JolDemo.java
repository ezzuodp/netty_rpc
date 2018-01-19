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
		Item o = new Item();
		/*
		com.ezweb.demo.JolDemo$Item object internals:
		 OFFSET  SIZE   TYPE DESCRIPTION                               VALUE
		      0     4        (object header)                           01 00 00 00 (00000001 00000000 00000000 00000000) (1)
		      4     4        (object header)                           00 00 00 00 (00000000 00000000 00000000 00000000) (0)
		      8     4        (object header)                           05 c1 00 f8 (00000101 11000001 00000000 11111000) (-134168315)
		     12     4        (loss due to the next object alignment)
		Instance size: 16 bytes
		Space losses: 0 bytes internal + 4 bytes external = 4 bytes total

		com.ezweb.demo.JolDemo$Item object internals:
		 OFFSET  SIZE   TYPE DESCRIPTION                               VALUE
		      0     4        (object header)                           88 f2 f8 02 (10001000 11110010 11111000 00000010) (49869448)
		      4     4        (object header)                           00 00 00 00 (00000000 00000000 00000000 00000000) (0)
		      8     4        (object header)                           05 c1 00 f8 (00000101 11000001 00000000 11111000) (-134168315)
		     12     4        (loss due to the next object alignment)
		Instance size: 16 bytes
		Space losses: 0 bytes internal + 4 bytes external = 4 bytes total
		 */
		synchronized (o) {
			ClassData classData = ClassData.parseInstance(o);
			CurrentLayouter currentLayouter = new CurrentLayouter();
			System.out.println(currentLayouter.layout(classData).toPrintable());
		}
		{
			ClassData classData = ClassData.parseInstance(o);
			CurrentLayouter currentLayouter = new CurrentLayouter();
			System.out.println(currentLayouter.layout(classData).toPrintable());
		}
	}
}
