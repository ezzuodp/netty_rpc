package com.ezweb.interview.shorturl;

import com.ezweb.interview.shorturl.idc.IDBuild;
import com.ezweb.interview.shorturl.idc.IDBuildImpl;
import com.ezweb.interview.shorturl.idl.IDMapper;
import com.ezweb.interview.shorturl.idl.IDMapperImpl;

/**
 * @author : zuodp
 * @version : 1.10
 */
public class ShortUrl {
	private static final String SHORT_URL_START = "http://t.cn/";
	private IDBuild idBuild = new IDBuildImpl();
	private IDMapper idMapper = new IDMapperImpl();

	public String url2shortUrl(String longUrl) {
		Long id = idBuild.buildId(longUrl);
		idMapper.url2Id(id, longUrl);
		return SHORT_URL_START + H64.long2Hex(id);
	}

	public String shortUrl2Url(String shortUrl) {
		// 解析出尾部
		String hex = shortUrl.substring(SHORT_URL_START.length());
		Long id = H64.hex2Long(hex);
		// 用ID找出对应的URL.
		return idMapper.id2Url(id);
	}

	public static void main(String[] args) {
		ShortUrl shortUrl = new ShortUrl();
		String longUrlBase = "https://www.baidu.com/aaaa=bcc";

		for (int i = 0; i < 100000; ++i) {
			String su = shortUrl.url2shortUrl(longUrlBase + "&i=" + i);
			String longUrl = shortUrl.shortUrl2Url(su);
			System.out.println("su = " + su);
			if (!longUrl.equals(longUrlBase + "&i=" + i)) {
				System.out.println("shortUrl = " + su + " 不唯一 => " + longUrl);
			}
		}
	}
}
