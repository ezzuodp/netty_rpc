package com.ezweb.interview.shorturl.cache;

import com.ezweb.interview.shorturl.url.NormalUrl;
import com.google.common.collect.Maps;

import java.util.Optional;
import java.util.concurrent.ConcurrentMap;

/**
 * 缓存 normalURL => ShortUrl
 *
 * @author : zuodp
 * @version : 1.10
 */
public class NormalUrlCache {

	private ConcurrentMap<String/*url.key*/, String/*short code*/> map = Maps.newConcurrentMap();

	public Optional<String> getShortCode(NormalUrl normalUrl) {
		String shortUrl = map.get(normalUrl.key());
		return Optional.of(shortUrl);
	}

	public void putShortCode(NormalUrl normalUrl, String shortCode) {
		map.putIfAbsent(normalUrl.key(), shortCode);
	}
}
