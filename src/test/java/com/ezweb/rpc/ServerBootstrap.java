package com.ezweb.rpc;

import com.ezweb.engine.CustProtoType;
import com.ezweb.engine.log.Log4j2System;
import com.ezweb.engine.rpc.RpcProtocol;
import com.ezweb.engine.rpc.serialize.kryo.KryoDecoder;
import com.ezweb.engine.rpc.serialize.kryo.KryoEncoder;
import com.ezweb.engine.rpc.server.RpcHandlerImpl;
import com.ezweb.engine.rpc.server.RpcProtocolImpl;
import com.ezweb.engine.rpc.server.RpcServerHandlerCreator;
import com.ezweb.engine.server.NettyServer;
import com.ezweb.rpc.simple.Hello;
import com.ezweb.rpc.simple.HelloImpl;

/**
 * @author : zuodp
 * @version : 1.10
 */
public class ServerBootstrap {

    public static void main(String[] args) throws InterruptedException {
        new Log4j2System("server").init(null);

        RpcHandlerImpl rpcHandler = new RpcHandlerImpl();
        rpcHandler.addExport(Hello.class, new HelloImpl());

        RpcProtocol kryoProtocol = new RpcProtocolImpl(new KryoDecoder(), new KryoEncoder());

        RpcServerHandlerCreator serverHandlerCreator = new RpcServerHandlerCreator();
        serverHandlerCreator.addRpcProtocol(CustProtoType.KRYO, kryoProtocol);
        serverHandlerCreator.addRpcHandler(CustProtoType.KRYO, rpcHandler);

        NettyServer nettyServer = new NettyServer(serverHandlerCreator);
        nettyServer.serve(9000);
        nettyServer.waitForClose();
    }
}
