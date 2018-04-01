package com.ezweb.interview.shorturl.cache;

import com.google.common.collect.Maps;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import org.bouncycastle.util.encoders.Hex;

import java.util.Optional;
import java.util.concurrent.ConcurrentMap;

/**
 * 缓存 normalURL => ShortUrl
 *
 * @author : zuodp
 * @version : 1.10
 */
public class NormalUrlCache {

	private HashFunction md5 = Hashing.md5();

	private ConcurrentMap<String/*md5*/, String/*short url*/> map = Maps.newConcurrentMap();

	public Optional<String> getShortUrl(String normalUrl) {
		byte[] bytes = md5
				.newHasher()
				.putUnencodedChars(normalUrl)
				.hash()
				.asBytes();
		String md5 = Hex.toHexString(bytes);
		String shortUrl = map.get(md5);
		return Optional.of(shortUrl);
	}

	public void putShortUrl(String normalUrl, String shortUrl) {
		byte[] bytes = md5
				.newHasher()
				.putUnencodedChars(normalUrl)
				.hash()
				.asBytes();
		String md5 = Hex.toHexString(bytes);
		map.putIfAbsent(md5, shortUrl);
	}
}
