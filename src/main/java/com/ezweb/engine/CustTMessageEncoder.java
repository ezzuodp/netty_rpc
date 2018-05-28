package com.ezweb.engine;

import com.ezweb.engine.util.PureJavaCrc32;
import com.ezweb.engine.util.UnsignedUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.ByteBuffer;

/**
 * @author : zuodp
 * @version : 1.10
 */
public class CustTMessageEncoder extends MessageToByteEncoder<CustTMessage> {
	public static final int FRAME_LEN = 4;

	/**
	 * Encode a message into a {@link ByteBuf}. This method will be called for each written message that can be handled
	 * by this encoder.
	 *
	 * @param ctx the {@link ChannelHandlerContext} which this {@link MessageToByteEncoder} belongs to
	 * @param msg the message to encode
	 * @param out the {@link ByteBuf} into which the encoded message will be written
	 * @throws Exception is thrown if an error accour
	 */
	@Override
	protected void encode(ChannelHandlerContext ctx, CustTMessage msg, ByteBuf out) throws Exception {
		// ONE frameLen = [head { magic:2 + [version | type]:1 + protoTYpe:1 + seqId:4 + len:4 }+ crc32 + body].
		out.writeInt(CustTMessage.HEAD_LEN + CustTMessage.CRC32_LEN + msg.getLen());

		// head
		out.writeShort(msg.getMagic());
		int vt = msg.getVersion() << 4 | msg.getType();
		out.writeByte(vt);
		out.writeByte(msg.getCodeType());
		out.writeInt(msg.getSeqId());
		out.writeInt(msg.getLen());

		// crc32
		long crc32 = calcCrc32(msg.getVersion(), msg.getType(), msg.getCodeType(), msg.getSeqId(), msg.getLen(), msg.getBody());// crc32 => uint32_t.
		out.writeBytes(UnsignedUtils.uint32ToBytes(crc32));

		// body
		if (msg.getLen() > 0)
			out.writeBytes(msg.getBody());
	}

	public static long calcCrc32(byte version, byte type, byte codeType, int seqId, int len, ByteBuffer body) {

		PureJavaCrc32 crc32 = new PureJavaCrc32();
		long v = CustTMessage.MAGIC;
		v = v << 4 | version;
		v = v << 4 | type;
		v = v << 8 | codeType;

		crc32.update(UnsignedUtils.uint32ToBytes(v), 0, 4);
		crc32.update(UnsignedUtils.uint32ToBytes(seqId), 0, 4);
		crc32.update(UnsignedUtils.uint32ToBytes(len), 0, 4);
		if (len > 0 && body != null)
			crc32.update(body.array(), 0, len);

		return crc32.getValue();
	}
}
