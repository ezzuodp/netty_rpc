package com.ezweb.engine;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author : zuodp
 * @version : 1.10
 */
public class CustTMessage {
	private final static AtomicInteger RequestId = new AtomicInteger(1);
	// frame ==> [head]{ magic:2 + [version | type]:1 + protoTYpe:1 + seqId:4 + body.len:4 } + [crc32:4] [body]
	public final static int MAGIC = 0xf11f;
	public final static int HEAD_LEN = 12;
	public final static int CRC32_LEN = 4;
	public final static int BODY_MAX_LEN = 8388607 - (HEAD_LEN + CRC32_LEN);

	private short magic = (short) MAGIC;
	private byte version = CustTVersion.VERSION_1;
	private byte type = 0;                      // 请求类型：CALL | REPLY | EXCEPTION
	private byte codeType = CustCodeType.NORMAL;// 协议编码类型
	private int seqId = 0;                      // 请求顺序号
	private int len = 0;                        // body.len
	private ByteBuffer body = null;

	public static CustTMessage newRequestMessage() {
		CustTMessage msg = new CustTMessage();
		msg.setType(CustTType.CALL);
		msg.setVersion(CustTVersion.VERSION_1);
		msg.setSeqId(RequestId.getAndIncrement());
		return msg;
	}

	public static CustTMessage newResponseMessage() {
		CustTMessage msg = new CustTMessage();
		msg.setType(CustTType.REPLY);
		msg.setVersion(CustTVersion.VERSION_1);
		return msg;
	}

	public int getLen() {
		return len;
	}

	public void setLen(int len) {
		this.len = len;
	}

	public short getMagic() {
		return magic;
	}

	public byte getVersion() {
		return version;
	}

	public void setVersion(byte version) {
		this.version = version;
	}

	public byte getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
	}

	public CustTMessage setSeqId(int seqId) {
		this.seqId = seqId;
		return this;
	}

	public int getSeqId() {
		return seqId;
	}

	public ByteBuffer getBody() {
		return body;
	}

	public CustTMessage setBody(ByteBuffer body) {
		this.body = body;
		return this;
	}

	public byte getCodeType() {
		return codeType;
	}

	public void setCodeType(byte codeType) {
		this.codeType = codeType;
	}

	@Override
	public String toString() {
		return String.format("<CustTMessage ver:%s type:%s seqId:%d>", version, type, seqId);
	}
}