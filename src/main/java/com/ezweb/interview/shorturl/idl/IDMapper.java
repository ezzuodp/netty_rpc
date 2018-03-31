package com.ezweb.interview.shorturl.idl;

/**
 * @author : zuodp
 * @version : 1.10
 */
public interface IDMapper {

	void url2Id(long id, String url);

	String id2Url(long id);
}
