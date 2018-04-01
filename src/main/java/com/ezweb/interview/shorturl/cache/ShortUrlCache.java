package com.ezweb.interview.shorturl.cache;

import com.google.common.collect.Maps;

import java.util.Optional;
import java.util.concurrent.ConcurrentMap;

/**
 * @author : zuodp
 * @version : 1.10
 */
public class ShortUrlCache {

	private ConcurrentMap<String/*shortUrl*/, String/*normalUrl*/> map = Maps.newConcurrentMap();

	public void put(String shortUrl, String normalUrl) {
		map.putIfAbsent(shortUrl, normalUrl);
	}

	public Optional<String> getNormalUrl(String shortUrl) {
		return Optional.of(map.get(shortUrl));
	}
}
