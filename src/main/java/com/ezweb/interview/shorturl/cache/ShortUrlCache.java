package com.ezweb.interview.shorturl.cache;

import com.ezweb.interview.shorturl.url.ShortUrl;
import com.google.common.collect.Maps;

import java.util.Optional;
import java.util.concurrent.ConcurrentMap;

/**
 * @author : zuodp
 * @version : 1.10
 */
public class ShortUrlCache {

	private ConcurrentMap<String/*shortCode*/, String/*normalUrl*/> map = Maps.newConcurrentMap();

	public void putNormalUrl(ShortUrl shortUrl, String normalUrl) {
		map.putIfAbsent(shortUrl.getShortCode(), normalUrl);
	}

	public Optional<String> getNormalUrl(ShortUrl shortUrl) {
		return Optional.of(map.get(shortUrl.getShortCode()));
	}
}
