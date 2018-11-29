package com.ezweb.engine.server;

/**
 * @author : zuodp
 * @version : 1.10
 */
public interface ServerHandlerCreator<H extends AbsServerHandler> {
	H create() throws Exception;
}
