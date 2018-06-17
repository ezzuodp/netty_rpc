package com.ezweb.engine.rpc.serialize.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.util.List;

/**
 * @author : zuodp
 * @version : 1.10
 */
public class StackTraceElementSerializer extends Serializer<StackTraceElement> {
	@Override
	public void write(Kryo kryo, Output output, StackTraceElement stackTraceElement) {
		output.writeString(stackTraceElement.getClassName());
		output.writeString(stackTraceElement.getMethodName());
		output.writeString(stackTraceElement.getFileName());
		output.writeInt(stackTraceElement.getLineNumber());
	}

	@Override
	public StackTraceElement read(Kryo kryo, Input input, Class<StackTraceElement> aClass) {
		return new StackTraceElement(input.readString(), input.readString(), input.readString(), input.readInt());
	}
}
