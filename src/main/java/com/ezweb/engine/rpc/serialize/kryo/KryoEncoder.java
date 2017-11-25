package com.ezweb.engine.rpc.serialize.kryo;

import com.esotericsoftware.kryo.io.ByteBufferOutputStream;
import com.esotericsoftware.kryo.io.Output;
import com.ezweb.engine.rpc.serialize.Encoder;
import org.xerial.snappy.SnappyOutputStream;

import java.nio.ByteBuffer;

public class KryoEncoder implements Encoder {
	public final static int MAX_SIZE = 1024 * 1024; //128K

	@Override
	public void encode(Object object, ByteBuffer outbytes) throws Exception {
		Output output = new Output(1024, MAX_SIZE);

		SnappyOutputStream outputStream = new SnappyOutputStream(new ByteBufferOutputStream(outbytes));
		output.setOutputStream(outputStream);

		KryoFactory.getDefaultFactory().getKryo().writeClassAndObject(output, object);
		output.flush();
	}
}
