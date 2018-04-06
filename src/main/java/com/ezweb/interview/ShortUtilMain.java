package com.ezweb.interview;

import com.ezweb.interview.shorturl.ShortUrlUtil;
import com.ezweb.interview.shorturl.url.NormalUrl;
import com.ezweb.interview.shorturl.url.ShortUrl;

/**
 * @author : zuodp
 * @version : 1.10
 */
public class ShortUtilMain {
	public static void main(String[] args) {
		ShortUrlUtil shortUrlUtil = new ShortUrlUtil();

		for (int i = 0; i < 1000; ++i) {
			NormalUrl normalUrl = new NormalUrl("http://www.baidu.com/index.html?a=b&_" + i);
			ShortUrl shortUrl = shortUrlUtil.normal2Short(normalUrl);
			NormalUrl result = shortUrlUtil.short2Normal(shortUrl);

			System.out.println("shortUrl =>>> " + shortUrl.getUrl());
			System.out.println("result = " + result.getUrl());
		}
		/*System.out.println("shortUrlUtil = " + shortUrlUtil);*/
	}
}
