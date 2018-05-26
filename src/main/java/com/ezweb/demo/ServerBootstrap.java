package com.ezweb.demo;

import com.ezweb.demo.simple.Hello;
import com.ezweb.demo.simple.HelloImpl;
import com.ezweb.engine.CustCodeType;
import com.ezweb.engine.log.Log4j2System;
import com.ezweb.engine.rpc.RpcProtocolCode;
import com.ezweb.engine.rpc.serialize.kryo.KryoSerializationImpl;
import com.ezweb.engine.rpc.server.RpcProtocolCodeImpl;
import com.ezweb.engine.rpc.server.RpcHandlerImpl;
import com.ezweb.engine.rpc.server.RpcServerHandlerCreator;
import com.ezweb.engine.server.NettyServer;

/**
 * @author : zuodp
 * @version : 1.10
 */
public class ServerBootstrap {

	public static void main(String[] args) throws InterruptedException {
		new Log4j2System("server").init(null);

		RpcHandlerImpl rpcHandler = new RpcHandlerImpl();
		rpcHandler.addExport(Hello.class, new HelloImpl());

		RpcProtocolCode normalRpcCodeProtocol = new RpcProtocolCodeImpl(new KryoSerializationImpl());

		RpcServerHandlerCreator serverHandlerCreator = new RpcServerHandlerCreator();
		serverHandlerCreator.addRpcHandler(rpcHandler);
		serverHandlerCreator.addRpcProtocol(CustCodeType.NORMAL, normalRpcCodeProtocol);

		NettyServer nettyServer = new NettyServer(serverHandlerCreator);
		nettyServer.serve(9000);
		nettyServer.waitForClose();
	}
}
