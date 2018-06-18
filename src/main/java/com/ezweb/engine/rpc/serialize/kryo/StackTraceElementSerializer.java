package com.ezweb.engine.rpc.serialize.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * @author : zuodp
 * @version : 1.10
 */
public class StackTraceElementSerializer extends Serializer<StackTraceElement> {
	public StackTraceElementSerializer() {
		this.setImmutable(false);
	}

	@Override
	public void write(Kryo kryo, Output output, StackTraceElement stackTraceElement) {
		output.writeAscii(stackTraceElement.getClassName());
		output.writeAscii(stackTraceElement.getMethodName());
		output.writeAscii(stackTraceElement.getFileName());
		output.writeVarInt(stackTraceElement.getLineNumber(), true);
	}

	@Override
	public StackTraceElement read(Kryo kryo, Input input, Class<StackTraceElement> aClass) {
		return new StackTraceElement(input.readString(), input.readString(), input.readString(), input.readVarInt(true));
	}
}
