package com.ezweb.engine;

import com.ezweb.interview.shorturl.H64;
import org.openjdk.jol.info.ClassData;
import org.openjdk.jol.layouters.CurrentLayouter;
import sun.misc.Contended;

/**
 * @author : zuodp
 * @version : 1.10
 */
public class JolDemo {
	static class Item {
		// 要注意的是user classpath使用此注解默认是无效的，需要在jvm启动时设置-XX:-RestrictContended
		@Contended
		public volatile long valueA;
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
		for (int i = 0; i < 100; ++i) {
			String f = H64.long2Hex((long) i);
			System.out.println("f = " + f);
		}
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

        Object o = new Item[21];
        21个对象引用占用：21 * 4 = 84 个字节.
		[Lcom.ezweb.demo.JolDemo$Item; object internals:
		 OFFSET  SIZE                          TYPE DESCRIPTION                               VALUE
		      0     4                               (object header)                           01 00 00 00 (00000001 00000000 00000000 00000000) (1)
		      4     4                               (object header)                           00 00 00 00 (00000000 00000000 00000000 00000000) (0)
		      8     4                               (object header)                           43 c1 00 f8 (01000011 11000001 00000000 11111000) (-134168253)
		     12     4                               (object header)                           15 00 00 00 (00010101 00000000 00000000 00000000) (21) --> 数组长度
		     16    84   com.ezweb.demo.JolDemo$Item JolDemo$Item;.<elements>                  N/A
		    100     4                               (loss due to the next object alignment)
		Instance size: 104 bytes
		Space losses: 0 bytes internal + 4 bytes external = 4 bytes total

		//--------------------------------------------------------------------------------------------
		typedef class   markOopDesc*  markOop;
		class oopDesc {
			  volatile markOop  _mark; // 指针:8个字节
			  union _metadata {
			    Klass*      _klass;    // 指针:8个字节
			    narrowKlass _compressed_klass; // jint32:4个字节
			  } _metadata;
		 }
		 class markOopDesc: public oopDesc {
		      uintptr_t value() const { return (uintptr_t) this; }
		 }
		 // 所以对象头未压缩时:8+8=16个字节，压缩后：8+4=12个字节 .
		 //--------------------------------------------------------------------------------------------

		 */
		Object o = new Item[21];
		{
			ClassData classData = ClassData.parseInstance(o);
			CurrentLayouter currentLayouter = new CurrentLayouter();
			System.out.println(currentLayouter.layout(classData).toPrintable());
		}
	}
}
