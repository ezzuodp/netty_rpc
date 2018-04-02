package com.ezweb.interview.shorturl.load;

import com.ezweb.interview.shorturl.url.NormalUrl;
import com.ezweb.interview.shorturl.url.ShortUrl;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author : zuodp
 * @version : 1.10
 */
public class UrlLoaderImpl implements UrlLoader {
	private Cache<String, String> normalUrlCache = CacheBuilder.newBuilder()
			.expireAfterAccess(30, TimeUnit.MINUTES)
			.build();

	private Cache<String, String> shortUrlCache = CacheBuilder.newBuilder()
			.expireAfterAccess(30, TimeUnit.MINUTES)
			.build();

	/**
	 * 先从缓存，再从持久化中加载已经生成的shortCode
	 */
	@Override
	public Optional<String> loadShortCode(NormalUrl normalUrl) {
		return Optional.ofNullable(normalUrlCache.getIfPresent(normalUrl.key()));
	}

	/**
	 * 先从缓存，再从持久化中根据shortCode加载标准URL.
	 */
	@Override
	public Optional<String> loadNormalUrl(ShortUrl shortUrl) {
		return Optional.ofNullable(shortUrlCache.getIfPresent(shortUrl.getShortCode()));
	}

	/**
	 * 缓存对照关系.
	 */
	@Override
	public void cacheShortUrl(ShortUrl shortUrl, NormalUrl url) {
		shortUrlCache.put(shortUrl.getShortCode(), url.getUrl());
		normalUrlCache.put(url.key(), shortUrl.getShortCode());
	}
}
