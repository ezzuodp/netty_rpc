package com.ezweb.engine.util;

import java.nio.ByteOrder;

/**
 * @author : zuodp
 * @version : 1.10
 */
public abstract class ByteUtils {
	public static long toLong(byte[] bytes) {
		return toLong(ByteOrder.BIG_ENDIAN, bytes);
	}

	public static long toLong(ByteOrder order, byte[] bytes) {
		return toLong(order, bytes, 0);
	}

	public static int toInt(byte[] bytes) {
		return toInt(ByteOrder.BIG_ENDIAN, bytes);
	}

	public static int toInt(ByteOrder order, byte[] bytes) {
		return toInt(order, bytes, 0);
	}

	public static short toShort(byte[] bytes) {
		return toShort(ByteOrder.BIG_ENDIAN, bytes, 0);
	}

	public static short toShort(ByteOrder order, byte[] bytes) {
		return toShort(order, bytes, 0);
	}

	public static long toLong(ByteOrder order, byte[] bytes, int offset) {
		if (offset + 8 > bytes.length) throw new IllegalArgumentException("bytes no has 8 byte space");
		if (order == ByteOrder.BIG_ENDIAN) {
			return ((long) (bytes[offset + 0] & 0xff) << 56) |
					((long) (bytes[offset + 1] & 0xff) << 48) |
					((long) (bytes[offset + 2] & 0xff) << 40) |
					((long) (bytes[offset + 3] & 0xff) << 32) |
					((long) (bytes[offset + 4] & 0xff) << 24) |
					((long) (bytes[offset + 5] & 0xff) << 16) |
					((long) (bytes[offset + 6] & 0xff) << 8) |
					((long) (bytes[offset + 7] & 0xff));
		} else {
			return ((long) (bytes[offset + 0] & 0xff)) |
					((long) (bytes[offset + 1] & 0xff) << 8) |
					((long) (bytes[offset + 2] & 0xff) << 16) |
					((long) (bytes[offset + 3] & 0xff) << 24) |
					((long) (bytes[offset + 4] & 0xff) << 32) |
					((long) (bytes[offset + 5] & 0xff) << 40) |
					((long) (bytes[offset + 6] & 0xff) << 48) |
					((long) (bytes[offset + 7] & 0xff) << 56);
		}
	}

	public static int toInt(ByteOrder order, byte[] bytes, int offset) {
		if (offset + 4 > bytes.length) throw new IllegalArgumentException("bytes no has 4 byte space");
		if (order == ByteOrder.BIG_ENDIAN) {
			return ((bytes[offset + 0] & 0xff) << 24) |
					((bytes[offset + 1] & 0xff) << 16) |
					((bytes[offset + 2] & 0xff) << 8) |
					((bytes[offset + 3] & 0xff));
		} else {
			return ((bytes[offset + 0] & 0xff)) |
					((bytes[offset + 1] & 0xff) << 8) |
					((bytes[offset + 2] & 0xff) << 16) |
					((bytes[offset + 3] & 0xff) << 24);
		}
	}

	public static short toShort(ByteOrder order, byte[] bytes, int offset) {
		if (offset + 2 > bytes.length) throw new IllegalArgumentException("bytes no has 2 byte space");
		if (order == ByteOrder.BIG_ENDIAN) {
			return (short) ((bytes[offset] & 0xff) << 8 | bytes[offset + 1] & 0xff);
		} else {
			return (short) (bytes[offset] & 0xff | (bytes[offset + 1] & 0xff) << 8);
		}
	}

	public static byte[] toBytes(short value) {
		return toBytes(ByteOrder.BIG_ENDIAN, value);
	}

	public static byte[] toBytes(ByteOrder order, short value) {
		byte[] buf = new byte[2];
		toBytes(order, buf, 0, value);
		return buf;
	}

	public static byte[] toBytes(int value) {
		return toBytes(ByteOrder.BIG_ENDIAN, value);
	}

	public static byte[] toBytes(ByteOrder order, int value) {
		byte[] buf = new byte[4];
		toBytes(order, buf, 0, value);
		return buf;
	}

	public static byte[] toBytes(long value) {
		return toBytes(ByteOrder.BIG_ENDIAN, value);
	}

	public static byte[] toBytes(ByteOrder order, long value) {
		byte[] buf = new byte[8];
		toBytes(order, buf, 0, value);
		return buf;
	}

	public static void toBytes(ByteOrder order, byte[] buf, int offset, long value) {
		if (offset + 8 > buf.length) throw new IllegalArgumentException("buf no has 8 byte space");
		if (order == ByteOrder.BIG_ENDIAN) {
			buf[offset + 0] = (byte) (0xff & (value >>> 56));
			buf[offset + 1] = (byte) (0xff & (value >>> 48));
			buf[offset + 2] = (byte) (0xff & (value >>> 40));
			buf[offset + 3] = (byte) (0xff & (value >>> 32));
			buf[offset + 4] = (byte) (0xff & (value >>> 24));
			buf[offset + 5] = (byte) (0xff & (value >>> 16));
			buf[offset + 6] = (byte) (0xff & (value >>> 8));
			buf[offset + 7] = (byte) (0xff & (value));
		} else {
			buf[offset + 0] = (byte) (0xff & (value));
			buf[offset + 1] = (byte) (0xff & (value >>> 8));
			buf[offset + 2] = (byte) (0xff & (value >>> 16));
			buf[offset + 3] = (byte) (0xff & (value >>> 24));
			buf[offset + 4] = (byte) (0xff & (value >>> 32));
			buf[offset + 5] = (byte) (0xff & (value >>> 40));
			buf[offset + 6] = (byte) (0xff & (value >>> 48));
			buf[offset + 7] = (byte) (0xff & (value >>> 56));
		}
	}

	public static void toBytes(ByteOrder order, byte[] buf, int offset, int value) {
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

	public static void toBytes(ByteOrder order, byte[] buf, int offset, short value) {
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
