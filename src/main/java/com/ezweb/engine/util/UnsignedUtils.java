package com.ezweb.engine.util;

import java.nio.ByteOrder;

/**
 * java中常用的无符号Int
 *
 * @author : zuodp
 * @version : 1.10
 */
public abstract class UnsignedUtils {
	public static long toUInt32(byte[] bytes) {
		return toUInt32(ByteOrder.BIG_ENDIAN, bytes);
	}

	public static long toUInt32(ByteOrder order, byte[] bytes) {
		return toUInt32(order, bytes, 0);
	}

	public static long toUInt32(ByteOrder order, byte[] bytes, int offset) {
		if (offset + 4 > bytes.length) throw new IllegalArgumentException("bytes no has 4 byte space");
		if (order == ByteOrder.BIG_ENDIAN) {
			return (((long) (bytes[offset + 0] & 0xff) << 24) |
					((long) (bytes[offset + 1] & 0xff) << 16) |
					((long) (bytes[offset + 2] & 0xff) << 8) |
				 	 (long) (bytes[offset + 3] & 0xff));
		} else {
			return (((long) (bytes[offset + 0] & 0xff)) |
					((long) (bytes[offset + 1] & 0xff) << 8) |
					((long) (bytes[offset + 2] & 0xff) << 16) |
					 (long) (bytes[offset + 3] & 0xff) << 24);
		}
	}

	public static byte[] uint32ToBytes(long value) {
		return uint32ToBytes(ByteOrder.BIG_ENDIAN, value);
	}

	public static byte[] uint32ToBytes(ByteOrder order, long value) {
		byte[] buf = new byte[4];
		uint32ToBytes(order, buf, 0, value);
		return buf;
	}

	private static void uint32ToBytes(ByteOrder order, byte[] buf, int offset, long value) {
		if (offset + 4 > buf.length) throw new IllegalArgumentException("buf no has 4 byte space");
		if (order == ByteOrder.BIG_ENDIAN) {
			buf[offset + 0] = (byte) (0xff & (value >>> 24));
			buf[offset + 1] = (byte) (0xff & (value >>> 16));
			buf[offset + 2] = (byte) (0xff & (value >>> 8));
			buf[offset + 3] = (byte) (0xff & (value));
		} else {
			buf[offset + 0] = (byte) (0xff & (value));
			buf[offset + 1] = (byte) (0xff & (value >>> 8));
			buf[offset + 2] = (byte) (0xff & (value >>> 16));
			buf[offset + 3] = (byte) (0xff & (value >>> 24));
		}
	}

	public static int toUInt16(byte[] bytes) {
		return toUInt16(ByteOrder.BIG_ENDIAN, bytes);
	}

	public static int toUInt16(ByteOrder order, byte[] bytes) {
		return toUInt16(order, bytes, 0);
	}

	public static int toUInt16(ByteOrder order, byte[] bytes, int offset) {
		if (offset + 2 > bytes.length) throw new IllegalArgumentException("bytes no has 2 byte space");
		if (order == ByteOrder.BIG_ENDIAN) {
			return (((int) (bytes[offset] & 0xff) << 8) |
				 	 (int) (bytes[offset + 1] & 0xff));
		} else {
			return (((int) (bytes[offset] & 0xff)) |
					((int) (bytes[offset + 1] & 0xff) << 8));
		}
	}

	public static byte[] uint16ToBytes(int value) {
		return uint16ToBytes(ByteOrder.BIG_ENDIAN, value);
	}

	public static byte[] uint16ToBytes(ByteOrder order, int value) {
		byte[] buf = new byte[4];
		uint16ToBytes(order, buf, 0, value);
		return buf;
	}

	private static void uint16ToBytes(ByteOrder order, byte[] buf, int offset, int value) {
		if (offset + 2 > buf.length) throw new IllegalArgumentException("buf no has 2 byte space");
		if (order == ByteOrder.BIG_ENDIAN) {
			buf[offset + 0] = (byte) (0xff & (value >>> 8));
			buf[offset + 1] = (byte) (0xff & (value));
		} else {
			buf[offset + 0] = (byte) (0xff & (value));
			buf[offset + 1] = (byte) (0xff & (value >>> 8));
		}
	}
}
