package com.ezweb.interview.shorturl;

import com.ezweb.interview.shorturl.build.ShortUrlBuilder;
import com.ezweb.interview.shorturl.build.ShortUrlBuilderImpl;
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
	private UrlLoader urlLoader = new UrlLoaderImpl();

	public ShortUrlUtil() {
		this(SHORT_URL_START);
	}

	public ShortUrlUtil(String prefix) {
		this.prefix = prefix;
	}

	public ShortUrl normal2Short(NormalUrl url) {
		// 1.1 得到已经生成 shortCode.
		Optional<String> shortCode = urlLoader.loadShortCode(url);
		if (shortCode.isPresent()) {
			return new ShortUrl(prefix, shortCode.get());
		}

		// 2. 新生成 短URL
		shortCode = shortUrlBuilder.buildShortCode(url);
		//noinspection OptionalIsPresent
		if (shortCode.isPresent()) {

			ShortUrl shortUrl = new ShortUrl(prefix, shortCode.get());
			urlLoader.saveUrlMapping(shortUrl, url);

			return shortUrl;
		}

		return null;
	}

	public NormalUrl short2Normal(ShortUrl url) {
		// 2. 找 持久化 短URL 长url
		Optional<String> normalUrl = urlLoader.loadNormalUrl(url);

		//noinspection OptionalIsPresent
		if (normalUrl.isPresent()) {
			return new NormalUrl(normalUrl.get());
		}

		return null;
	}
}
