package com.ezweb.engine.util;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.QueryParams;
import com.ecwid.consul.v1.Response;
import com.ecwid.consul.v1.agent.model.NewService;
import com.ecwid.consul.v1.catalog.model.CatalogService;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author : zuodp
 * @version : 1.10
 */
public class ConsulUtils {
	private static NewService convertService(String name, String id, String host, int port, int ttl, String... tags) {
		NewService newService = new NewService();
		newService.setName(name);
		newService.setId(id);
		newService.setAddress(host);
		newService.setPort(port);
		newService.setTags(Arrays.asList(tags));

		NewService.Check check = new NewService.Check();
		check.setTtl(ttl + "s");
		newService.setCheck(check);
		return newService;
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		// demo service 服务组
		ConsulClient client = new ConsulClient("localhost", 8500);
		{
			NewService serviceConfig = convertService("demo_group", "172.16.1.1:8200/com.hello.world.api.helloService",
					"172.16.1.1", 8200, 30, "timeout=1000", "protocol=montan", "serial=fastjson"
			);
			client.agentServiceRegister(serviceConfig);
		}
		{
			NewService serviceConfig = convertService("demo_group", "172.16.1.2:8200/com.hello.world.api.helloService",
					"172.16.1.2", 8200, 30, "timeout=1000", "protocol=montan", "serial=javabin"
			);
			client.agentServiceRegister(serviceConfig);
		}
		Response<List<CatalogService>> reps = client.getCatalogService("demo_group", new QueryParams("dc1"));
		System.out.println("reps = " + reps.getConsulIndex());
		for (CatalogService service : reps.getValue()) {
			System.out.println("service = " + service.getServiceId());
			System.out.println("service = " + service.getAddress());
			System.out.println("service = " + service.getServicePort());
			System.out.println("service = " + service.getServiceTags());
		}
		TimeUnit.MINUTES.sleep(3L);
	}
}
