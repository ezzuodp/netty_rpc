package com.ezweb.engine;

/**
 * 类型,最大值 0b1111
 *
 * @author : zuodp
 * @version : 1.10
 */
public interface CustTType {
	byte ADMINCMD = 0;  // 管理控制台
	// RPC
	byte CALL = 1;
	byte REPLY = 2;
	byte ONEWAY = 3;      // 请求后不需要等待近回值.

	byte HEARTBEAT = 0xf; // 心跳最大值
}
