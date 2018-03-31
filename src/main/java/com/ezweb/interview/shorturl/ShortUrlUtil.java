package com.ezweb.interview.shorturl;

import com.ezweb.interview.shorturl.url.NormalUrl;
import com.ezweb.interview.shorturl.url.ShortUrl;

/**
 * @author : zuodp
 * @version : 1.10
 */
public class ShortUrlUtil {
	private static final String SHORT_URL_START = "http://t.cn/";

	private String prefix = null;

	public ShortUrlUtil() {
		prefix = SHORT_URL_START;
	}

	public ShortUrl normal2Short(NormalUrl url) {
		// 1. 判断已经生成shortUrl.

		// 2. 生成 短URL

		// 3. 缓存<Key:短URL, Val:长url>

		return null;
	}

	public NormalUrl short2Normal(ShortUrl ShortUrl) {
		// 1. 找 缓存<Key:短URL, Val:长url>
		// 2. 找 DataBase 短URL
		// 3. 返回 长url <异步缓存>
		return null;
	}
}
