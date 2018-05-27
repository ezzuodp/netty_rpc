package com.ezweb.engine.rpc.serialize.kryo;

import com.esotericsoftware.kryo.io.ByteBufferInput;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.ezweb.engine.rpc.serialize.Serialization;

import java.io.ByteArrayOutputStream;

/**
 * @author : zuodp
 * @version : 1.10
 */
public class KryoSerializationImpl implements Serialization {
	public final static int MAX_SIZE = 1024 * 1024; //128K
	private final static byte[] EMPTY = new byte[]{'0'};

	@Override
	public <T> T decode(byte[] bytes) {
		if (bytes == null || bytes.length == 0) return null;
		if (bytes.length == 1 && bytes[0] == EMPTY[0]) return null;

		Input input = new ByteBufferInput(bytes);
		return (T) KryoFactory.getDefaultFactory().getKryo().readClassAndObject(input);
	}

	@Override
	public byte[] encode(Object object) {
		if (object == null) return EMPTY;

		ByteArrayOutputStream out = new ByteArrayOutputStream(1024);

		Output output = new Output(1024, MAX_SIZE);
		output.setOutputStream(out);

		KryoFactory.getDefaultFactory().getKryo().writeClassAndObject(output, object);
		output.flush();

		return out.toByteArray();
	}

	@Override
	public <T> byte[] encodeNoType(T object) {
		if (object == null) return EMPTY;

		ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
		Output output = new Output(1024, MAX_SIZE);

		output.setOutputStream(out);

		KryoFactory.getDefaultFactory().getKryo().writeObject(output, object);
		output.flush();

		return out.toByteArray();
	}

	@Override
	public <T> T decodeNoType(byte[] bytes, Class<T> tType) {
		if (bytes == null || bytes.length == 0) return null;
		if (bytes.length == 1 && bytes[0] == EMPTY[0]) return null;

		Input input = new ByteBufferInput(bytes);
		return (T) KryoFactory.getDefaultFactory().getKryo().readObject(input, tType);
	}
}
