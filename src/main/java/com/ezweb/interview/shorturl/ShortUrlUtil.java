package com.ezweb.interview.shorturl;

import com.ezweb.interview.shorturl.build.ShortUrlBuilder;
import com.ezweb.interview.shorturl.cache.NormalUrlCache;
import com.ezweb.interview.shorturl.cache.ShortUrlCache;
import com.ezweb.interview.shorturl.url.NormalUrl;
import com.ezweb.interview.shorturl.url.ShortUrl;

import java.util.Optional;

/**
 * @author : zuodp
 * @version : 1.10
 */
public class ShortUrlUtil {
	private static final String SHORT_URL_START = "http://t.cn/";

	private final String prefix;

	private ShortUrlBuilder shortUrlBuilder = null;

	private NormalUrlCache normalUrlCache = new NormalUrlCache();
	private ShortUrlCache shortUrlcache = new ShortUrlCache();

	public ShortUrlUtil() {
		prefix = SHORT_URL_START;
	}

	public ShortUrl normal2Short(NormalUrl url) {
		// 1. 判断已经生成 shortUrl.
		Optional<String> shortUrl = normalUrlCache.getShortUrl(url.getUrl());
		if (shortUrl.isPresent()) {
			return new ShortUrl(shortUrl.get());
		}

		// 2. 生成 短URL
		shortUrl = shortUrlBuilder.buildShortUrl(url.getUrl());

		// 3. 缓存<Key:短URL, Val:长url>
		shortUrlcache.put(shortUrl.get(), url.getUrl());

		return new ShortUrl(prefix + shortUrl.get());
	}

	public NormalUrl short2Normal(ShortUrl url) {
		String shortUrl = url.getUrl().substring(prefix.length());
		// 1. 找 缓存<Key:短URL, Val:长url>
		Optional<String> normalUrl = shortUrlcache.getNormalUrl(shortUrl);

		// 2. 找 持久化 短URL 长url
		if (!normalUrl.isPresent()) {
			normalUrl = Optional.of(shortUrlBuilder.loadNormal(shortUrl));
			shortUrlcache.put(shortUrl, normalUrl.get());
		}

		// 3. 返回 长url <异步缓存>
		if (normalUrl.isPresent()) {
			return new NormalUrl(normalUrl.get());
		}

		return null;
	}
}
