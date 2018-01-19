package com.ezweb.demo;

import org.openjdk.jol.info.ClassData;
import org.openjdk.jol.info.ClassLayout;
import org.openjdk.jol.layouters.CurrentLayouter;

/**
 * @author : zuodp
 * @version : 1.10
 */
public class JolDemo {
	//
	// 类属性按照如下优先级进行排列：长整型和双精度类型；整型和浮点型；字符和短整型；字节类型和布尔类型，最后是引用类型。
	// 这些属性都按照各自的单位对齐。
	//
	static class Item {
		private String head;
		private String []xxhead;
	}
	/*
	0. 不论32还是64位，对象字节都是以8字节为padding。
	1. 基本数据类型（long,double,float,int,short,char,byte,boolean）占用的字节数，JVM规范中有明确的规定，无论是在32位还是64位的虚拟机，占用的内存大小是相同的。
	               8     8      4     4   2     2     1    1
	2. reference类型在32位JVM下占用4个字节，但是在64位下可能占用4个字节或8个字节，这取决于是否启用了64位JVM的指针压缩参数UseCompressedOops。
	   64位初始就是开启了 UseCompressedOops
	3. new Object()这个对象在32位JVM上占8个字节，在64位JVM上占16个字节。
	4. 开启(-XX:+UseCompressedOops)指针压缩，对象头占12字节; 关闭(-XX:-UseCompressedOops)指针压缩,对象头占16字节。
	5. 64位JVM上，数组对象的对象头占用24个字节，启用压缩之后占用16个字节。之所以比普通对象占用内存多是因为需要额外的空间存储数组的长度。
		指针压缩, 对象头占用12个字节,数组长度占用4个字节。所以是16个字节.
	  非指针压缩, 对象头占用16个字节,数组长度占用4个字节。pading 4 个字节，所以是24个字节.
	6. 对象内存布局中的实例数据，不包括类的static字段的大小，因为static字段是属于类的，被该类的所有对象共享。
	 */

	public static void main(String[] args) {
		ClassData clsData = ClassData.parseInstance(new Item[0]);

		CurrentLayouter currentLayouter = new CurrentLayouter();
		ClassLayout classLayout = currentLayouter.layout(clsData);
		System.out.println(classLayout.toPrintable());
		/*
		[Lcom.ezweb.demo.JolDemo$Item; object internals:
		 OFFSET  SIZE                          TYPE DESCRIPTION                               VALUE
		      0     4                               (object header)                           01 00 00 00 (00000001 00000000 00000000 00000000) (1)
		      4     4                               (object header)                           00 00 00 00 (00000000 00000000 00000000 00000000) (0)
		      8     4                               (object header)                           44 c1 00 20 (01000100 11000001 00000000 00100000) (536920388)
		     12     4                               (object header)                           00 00 00 00 (00000000 00000000 00000000 00000000) (0)
		     16     0   com.ezweb.demo.JolDemo$Item JolDemo$Item;.<elements>                  N/A
		 */
		/*
				 OFFSET  SIZE                          TYPE DESCRIPTION                               VALUE
		      0     4                               (object header)                           01 00 00 00 (00000001 00000000 00000000 00000000) (1)
		      4     4                               (object header)                           00 00 00 00 (00000000 00000000 00000000 00000000) (0)
		      8     4                               (object header)                           40 37 99 28 (01000000 00110111 10011001 00101000) (681129792)
		     12     4                               (object header)                           c0 7f 00 00 (11000000 01111111 00000000 00000000) (32704)
		     16     4                               (object header)                           00 00 00 00 (00000000 00000000 00000000 00000000) (0)
		     20     4                               (alignment/padding gap)
		     24     0   com.ezweb.demo.JolDemo$Item JolDemo$Item;.<elements>                  N/A
		 */
	}
}