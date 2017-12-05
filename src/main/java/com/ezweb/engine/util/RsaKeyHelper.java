package com.ezweb.engine.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.bouncycastle.asn1.ASN1OutputStream;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DLSequence;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.security.*;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.*;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <一句话说明功能>
 * <功能详细描述>
 *
 * @author zuodengpeng
 * @version 1.0.0
 * @date 2017/11/23
 */
public abstract class RsaKeyHelper {
	static {
		Security.addProvider(new BouncyCastleProvider());
	}

	private static Charset UTF8 = Charset.forName("UTF-8");

	private static final String BEGIN = "-----BEGIN";
	private static final Pattern PEM_DATA = Pattern.compile("-----BEGIN (.*)-----(.*)-----END (.*)-----", Pattern.DOTALL);

	public static KeyPair parseKeyPair(String pemData) {
		Matcher m = PEM_DATA.matcher(pemData.trim());

		if (!m.matches()) {
			/*try {
				return new KeyPair(extractPublicKey(pemData), null);
			} catch (Exception e) {*/
			throw new IllegalArgumentException("String is not PEM encoded data, nor a public key encoded for ssh");
			/*}*/
		}

		String type = m.group(1);
		final byte[] content = base64Decode(m.group(2));

		PublicKey publicKey;
		PrivateKey privateKey = null;

		try {
			KeyFactory fact = KeyFactory.getInstance("RSA", "BC");
			if (type.equals("RSA PRIVATE KEY")) { // PEM 私钥
				ASN1Sequence seq = ASN1Sequence.getInstance(content);
				if (seq.size() != 9) {
					throw new IllegalArgumentException("Invalid RSA Private Key ASN1 sequence.");
				}
				org.bouncycastle.asn1.pkcs.RSAPrivateKey key = org.bouncycastle.asn1.pkcs.RSAPrivateKey.getInstance(seq);
				RSAPublicKeySpec pubSpec = new RSAPublicKeySpec(key.getModulus(), key.getPublicExponent());
				RSAPrivateCrtKeySpec privSpec = new RSAPrivateCrtKeySpec(
						key.getModulus(), key.getPublicExponent(),
						key.getPrivateExponent(), key.getPrime1(),
						key.getPrime2(), key.getExponent1(),
						key.getExponent2(), key.getCoefficient());
				publicKey = fact.generatePublic(pubSpec);
				privateKey = fact.generatePrivate(privSpec);
			} else {
				throw new IllegalArgumentException(type + " is not a supported format");
			}

			return new KeyPair(publicKey, privateKey);
		} catch (InvalidKeySpecException | NoSuchProviderException e) {
			throw new RuntimeException(e);
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException(e);
		}
	}

	public static KeyPair generateKeyPair(int size) {
		if (size < 1024) size = 1024;
		if (size > 2048) size = 2048;
		try {
			final KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA", "BC");
			keyGen.initialize(size);
			return keyGen.generateKeyPair();
		} catch (NoSuchAlgorithmException | NoSuchProviderException e) {
			throw new IllegalStateException(e);
		}
	}

	private static final Pattern SSH_PUB_KEY = Pattern.compile("ssh-(rsa|dsa) ([A-Za-z0-9/+]+=*) (.*)", Pattern.DOTALL);

	public static RSAPublicKey parsePublicKey(String key) {

		Matcher m = SSH_PUB_KEY.matcher(key.trim());

		if (m.matches()) {
			String alg = m.group(1);
			String encKey = m.group(2);
			// String id = m.group(3);

			if (!"rsa".equalsIgnoreCase(alg)) {
				throw new IllegalArgumentException("Only RSA is currently supported, but algorithm was " + alg);
			}

			return parseSSHPublicKey(encKey);
		} else if (!key.startsWith(BEGIN)) {
			// Assume it's the plain Base64 encoded ssh key without the
			// "ssh-rsa" at the start
			return parseSSHPublicKey(key);
		} else {
			m = PEM_DATA.matcher(key.trim());

			if (!m.matches()) {
				throw new IllegalArgumentException("String is not PEM encoded data, nor a public key encoded for ssh");
			}
			String type = m.group(1);
			final byte[] content = base64Decode(m.group(2));
			try {
				KeyFactory fact = KeyFactory.getInstance("RSA", "BC");
				if (type.equals("RSA PUBLIC KEY")) { // TODO:可以解，还没有找到方法输出.
					ASN1Sequence seq = ASN1Sequence.getInstance(content);
					org.bouncycastle.asn1.pkcs.RSAPublicKey bc_key = org.bouncycastle.asn1.pkcs.RSAPublicKey.getInstance(seq);
					RSAPublicKeySpec pubSpec = new RSAPublicKeySpec(bc_key.getModulus(), bc_key.getPublicExponent());
					return (RSAPublicKey) fact.generatePublic(pubSpec);
				} else if (type.equals("PUBLIC KEY")) { // PEM 格式公钥
					KeySpec keySpec = new X509EncodedKeySpec(content);
					return (RSAPublicKey) fact.generatePublic(keySpec);
				} else {
					throw new IllegalArgumentException(type + " is not a supported format");
				}
			} catch (InvalidKeySpecException | NoSuchProviderException e) {
				throw new RuntimeException(e);
			} catch (NoSuchAlgorithmException e) {
				throw new IllegalStateException(e);
			}
		}
	}

	private static final byte[] SSH_RSA_PREFIX = new byte[]{0, 0, 0, 7, 's', 's', 'h', '-', 'r', 's', 'a'};

	public static String fmtSSHPublicKey(RSAPublicKey key, String id) {
		StringWriter output = new StringWriter();
		output.append("ssh-rsa ");
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		try {
			stream.write(SSH_RSA_PREFIX);
			writeBigInteger(stream, key.getPublicExponent());
			writeBigInteger(stream, key.getModulus());
		} catch (IOException e) {
			throw new IllegalStateException("Cannot encode key", e);
		}
		output.append(base64Encode(stream.toByteArray()));
		output.append(" ").append(id);
		return output.toString();
	}

	// PEM: DER经过base64编码转换为PEM格式
	public static String fmtPEMPublicKey(RSAPublicKey key) {
		StringWriter output = new StringWriter();

		ByteBuf byteBuf = Unpooled.wrappedBuffer(key.getEncoded());
		ByteBuf base64Buf = io.netty.handler.codec.base64.Base64.encode(byteBuf, true);

		output.append("-----BEGIN PUBLIC KEY-----\n");
		output.append(base64Buf.toString(UTF8)).append('\n');
		output.append("-----END PUBLIC KEY-----\n");
		return output.toString();
	}

	// DER: 原始的RSA Key按照ASN1 DER编码的方式存储
	public static byte[] fmtDERPublicKey(RSAPublicKey key) {
		return key.getEncoded();
	}

	private static byte[] base64Decode(String string) {
		try {
			ByteBuffer bytes = UTF8.newEncoder().encode(CharBuffer.wrap(string));
			byte[] bytesCopy = new byte[bytes.limit()];
			System.arraycopy(bytes.array(), 0, bytesCopy, 0, bytes.limit());
			return Base64.decode(bytesCopy);
		} catch (CharacterCodingException e) {
			throw new RuntimeException(e);
		}
	}

	private static String base64Encode(byte[] bytes) {
		try {
			return UTF8.newDecoder().decode(ByteBuffer.wrap(Base64.encode(bytes))).toString();
		} catch (CharacterCodingException e) {
			throw new RuntimeException(e);
		}
	}

	private static RSAPublicKey parseSSHPublicKey(String encKey) {
		ByteArrayInputStream in = new ByteArrayInputStream(base64Decode(encKey));

		byte[] prefix = new byte[SSH_RSA_PREFIX.length];

		try {
			if (in.read(prefix) != SSH_RSA_PREFIX.length || !Arrays.equals(SSH_RSA_PREFIX, prefix)) {
				throw new IllegalArgumentException("SSH key prefix not found");
			}

			BigInteger e = new BigInteger(readBigInteger(in));
			BigInteger n = new BigInteger(readBigInteger(in));

			return createPublicKey(n, e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static RSAPublicKey createPublicKey(BigInteger n, BigInteger e) {
		try {
			return (RSAPublicKey) KeyFactory.getInstance("RSA", "BC").generatePublic(new RSAPublicKeySpec(n, e));
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	private static void writeBigInteger(ByteArrayOutputStream stream, BigInteger num) throws IOException {
		int length = num.toByteArray().length;
		byte[] data = new byte[4];
		data[0] = (byte) ((length >> 24) & 0xFF);
		data[1] = (byte) ((length >> 16) & 0xFF);
		data[2] = (byte) ((length >> 8) & 0xFF);
		data[3] = (byte) (length & 0xFF);
		stream.write(data);
		stream.write(num.toByteArray());
	}

	private static byte[] readBigInteger(ByteArrayInputStream in) throws IOException {
		byte[] b = new byte[4];

		if (in.read(b) != 4) {
			throw new IOException("Expected length data as 4 bytes");
		}

		int l = ((b[0] & 0xFF) << 24) | ((b[1] & 0xFF) << 16) | ((b[2] & 0xFF) << 8) | (b[3] & 0xFF);

		b = new byte[l];

		if (in.read(b) != l) {
			throw new IOException("Expected " + l + " key bytes");
		}

		return b;
	}
}
