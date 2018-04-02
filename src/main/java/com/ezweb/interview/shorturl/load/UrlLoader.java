package com.ezweb.interview.shorturl.load;

import com.ezweb.interview.shorturl.url.NormalUrl;
import com.ezweb.interview.shorturl.url.ShortUrl;

import java.util.Optional;

/**
 * @author : zuodp
 * @version : 1.10
 */
public interface UrlLoader {
	/**
	 * 先从缓存，再从持久化中加载已经生成的shortCode
	 */
	Optional<String> loadShortCode(NormalUrl normalUrl);

	/**
	 * 先从缓存，再从持久化中根据shortCode加载标准URL.
	 */
	Optional<String> loadNormalUrl(ShortUrl shortUrl);

	/**
	 * 缓存对照关系.
	 */
	void cacheShortUrl(ShortUrl shortUrl, NormalUrl url);
}
