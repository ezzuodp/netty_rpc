package com.ezweb.engine.rpc.serialize.kryo;

import com.esotericsoftware.kryo.io.ByteBufferInput;
import com.esotericsoftware.kryo.io.Input;
import com.ezweb.engine.rpc.serialize.Decoder;
import org.xerial.snappy.SnappyInputStream;

import java.nio.ByteBuffer;

public class KryoDecoder implements Decoder {
	@Override
	public Object decode(String className, ByteBuffer bytes) throws Exception {
		SnappyInputStream inputStream = new SnappyInputStream(
				new ByteBufferInput(bytes)
		);
		Input input = new Input(inputStream);
		return KryoFactory.getDefaultFactory().getKryo().readClassAndObject(input);
	}
}
