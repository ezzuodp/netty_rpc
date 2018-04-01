package com.ezweb.interview.shorturl.url;

import com.ezweb.interview.shorturl.encode.Hex;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

/**
 * @author : zuodp
 * @version : 1.10
 */
public interface IUrl {
	String getUrl();

	default String key() {
		// 用MD5将normalUrl变短.
		HashFunction md5 = Hashing.md5();
		byte[] v = md5.newHasher().putUnencodedChars(this.getUrl()).hash().asBytes();
		return Hex.fromByteArray(v);
	}
}
