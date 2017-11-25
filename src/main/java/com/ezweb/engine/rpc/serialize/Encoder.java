
package com.ezweb.engine.rpc.serialize;

import java.nio.ByteBuffer;

public interface Encoder {

	/**
	 * Encode Object to byte[]
	 */
	void encode(Object object, ByteBuffer outbytes) throws Exception;

}
