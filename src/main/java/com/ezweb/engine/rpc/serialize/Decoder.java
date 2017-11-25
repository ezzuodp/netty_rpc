package com.ezweb.engine.rpc.serialize;

import java.nio.ByteBuffer;

public interface Decoder {

	/**
	 * decode byte[] to Object
	 */
	Object decode(String className, ByteBuffer bytes) throws Exception;

}
