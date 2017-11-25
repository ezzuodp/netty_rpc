package com.ezweb.engine.rpc;

/**
 * @author zuodengpeng
 * @date 2017-08-07
 * @since 1.0.0
 */
public interface RpcResponse {
    Object getValue();

    void setValue(Object value);

    Exception getException();

    void setException(Exception exception);
}
