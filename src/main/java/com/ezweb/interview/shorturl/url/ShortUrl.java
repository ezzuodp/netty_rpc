package com.ezweb.interview.shorturl.url;

/**
 * @author : zuodp
 * @version : 1.10
 */
public class ShortUrl implements IUrl {
	private String url;

	public ShortUrl() {
	}

	public ShortUrl(String url) {
		this.url = url;
	}

	@Override
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
