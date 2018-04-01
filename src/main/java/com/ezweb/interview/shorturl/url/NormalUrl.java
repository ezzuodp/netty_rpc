package com.ezweb.interview.shorturl.url;

/**
 * @author : zuodp
 * @version : 1.10
 */
public class NormalUrl implements IUrl {
	private String url;

	public NormalUrl() {
	}

	public NormalUrl(String url) {
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
