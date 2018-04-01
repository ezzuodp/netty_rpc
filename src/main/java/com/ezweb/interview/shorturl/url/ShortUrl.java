package com.ezweb.interview.shorturl.url;

/**
 * @author : zuodp
 * @version : 1.10
 */
public class ShortUrl implements IUrl {
	private String prefix;
	private String shortCode;

	public ShortUrl(String prefix, String shortCode) {
		this.prefix = prefix;
		this.shortCode = shortCode;
	}

	@Override
	public String getUrl() {
		return prefix + shortCode;
	}

	public String getShortCode() {
		return shortCode;
	}
}
