package com.ezweb.interview.shorturl;

import com.ezweb.interview.shorturl.build.ShortUrlBuilder;
import com.ezweb.interview.shorturl.build.ShortUrlBuilderImpl;
import com.ezweb.interview.shorturl.cache.NormalUrlCache;
import com.ezweb.interview.shorturl.cache.ShortUrlCache;
import com.ezweb.interview.shorturl.load.UrlLoader;
import com.ezweb.interview.shorturl.load.UrlLoaderImpl;
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

	private ShortUrlBuilder shortUrlBuilder = new ShortUrlBuilderImpl();
	private UrlLoader loader = new UrlLoaderImpl();

	private NormalUrlCache normalUrlCache = new NormalUrlCache();
	private ShortUrlCache shortUrlcache = new ShortUrlCache();

	public ShortUrlUtil() {
		this(SHORT_URL_START);
	}

	public ShortUrlUtil(String prefix) {
		this.prefix = prefix;
	}

	public ShortUrl normal2Short(NormalUrl url) {
		// 1. 判断是否已经生成 shortCode.
		Optional<String> shortCode = normalUrlCache.getShortCode(url);

		if (shortCode.isPresent()) {
			return new ShortUrl(prefix, shortCode.get());
		}

		// 1.1 得到已经生成 shortCode.
		shortCode = loader.loadShortCode(url.getUrl());
		if (shortCode.isPresent()) {
			ShortUrl shortUrl = new ShortUrl(prefix, shortCode.get());

			// 重入缓存
			shortUrlcache.putNormalUrl(shortUrl, url.getUrl());
			normalUrlCache.putShortCode(url, shortUrl.getShortCode());

			return shortUrl;
		}

		// 2. 新生成 短URL
		shortCode = shortUrlBuilder.buildShortCode(url.getUrl());
		ShortUrl shortUrl = new ShortUrl(prefix, shortCode.get());

		// 3. 缓存
		shortUrlcache.putNormalUrl(shortUrl, url.getUrl());
		normalUrlCache.putShortCode(url, shortUrl.getShortCode());

		return shortUrl;
	}

	public NormalUrl short2Normal(ShortUrl url) {
		String shortCode = url.getShortCode();

		// 1. 找 缓存<Key:短URL, Val:长url>
		Optional<String> normalUrl = shortUrlcache.getNormalUrl(url);
		if (normalUrl.isPresent()) {
			return new NormalUrl(normalUrl.get());
		}

		// 2. 找 持久化 短URL 长url
		normalUrl = loader.loadNormalUrl(shortCode);
		if (normalUrl.isPresent()) {
			// 重入缓存
			shortUrlcache.putNormalUrl(url, normalUrl.get());
			return new NormalUrl(normalUrl.get());
		}

		return null;
	}
}
