package com.ezweb.engine.rpc.serialize;

/**
 * @author : zuodp
 * @version : 1.10
 */
public interface Serialization {
	<T> byte[] encode(T object) throws Exception;

	<T> T decode(byte[] bytes) throws Exception;
}
