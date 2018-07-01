package com.ezweb.engine;

/**
 * 类型,最大值 0b1111
 *
 * @author : zuodp
 * @version : 1.10
 */
public interface CustTType {
	byte HEARTBEAT = 0;  // 心跳

	byte CALL = 1;
	byte REPLY = 2;
	byte ONEWAY = 3;      // 请求后不需要等待近回值.

	byte MAX_VALUE = 0xf;
}
