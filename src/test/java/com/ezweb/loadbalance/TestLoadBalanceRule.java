package com.ezweb.loadbalance;

import com.ezweb.engine.balance.Server;
import com.ezweb.engine.balance.rule.LvsRoundRobinBalancer;
import com.ezweb.engine.balance.rule.NginxRoundRobinBalancer;
import com.ezweb.engine.balance.rule.RoundRobinBalancer;
import com.google.common.collect.Lists;
import org.apache.logging.log4j.util.Strings;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author : zuodp
 * @version : 1.10
 */
public class TestLoadBalanceRule {
	private static class TestServer extends Server {
		private String id;

		public TestServer(String id, int weight) {
			super(weight);
			this.id = id;
		}

		public String getId() {
			return id;
		}
	}

	private static List<TestServer> init() {
		return Lists.newArrayList(
				new TestServer("1", 5),
				new TestServer("2", 2),
				new TestServer("3", 3)
		);
	}

	@Test
	public void testNginxRoundRobinRule() {
		Map<String, AtomicInteger> selResult = new HashMap<>();
		List<TestServer> serverList = init();

		NginxRoundRobinBalancer<TestServer> x = new NginxRoundRobinBalancer<>();

		for (TestServer server : serverList) {
			x.addServer(server);
			selResult.put(server.getId(), new AtomicInteger(0));
		}
		for (int i = 0; i < 100; ++i) {
			TestServer sel = x.choose();
			selResult.get(sel.getId()).incrementAndGet();
		}

		Assert.assertEquals(selResult.get("1").get(), 50);
		Assert.assertEquals(selResult.get("2").get(), 20);
		Assert.assertEquals(selResult.get("3").get(), 30);
	}

	@Test
	public void testNginxRoundRobinRule2() {
		List<TestServer> serverList = Lists.newArrayList(
				new TestServer("a", 5),
				new TestServer("b", 1),
				new TestServer("c", 1)
		);

		NginxRoundRobinBalancer<TestServer> ngx = new NginxRoundRobinBalancer<>();
		for (TestServer server : serverList) {
			ngx.addServer(server);
		}

		LvsRoundRobinBalancer<TestServer> lvs = new LvsRoundRobinBalancer<>();
		for (TestServer server : serverList) {
			lvs.addServer(server);
		}

		// 期望:>>>> (a, a, b, a, c, a, a)
		List<String> ngx_sel_ids = new ArrayList<>();
		String ngx_sel_id;
		for (int i = 0; i < 7; ++i) {
			TestServer sel = ngx.choose();
			ngx_sel_ids.add(sel.getId());
		}
		ngx_sel_id = Strings.join(ngx_sel_ids, ',');
		Assert.assertEquals(ngx_sel_id, "a,a,b,a,c,a,a");

		List<String> lvs_sel_ids = new ArrayList<>();
		String lvs_sel_id;
		for (int i = 0; i < 7; ++i) {
			TestServer sel = lvs.choose();
			lvs_sel_ids.add(sel.getId());
		}
		lvs_sel_id = Strings.join(lvs_sel_ids, ',');
		Assert.assertEquals(lvs_sel_id, "a,a,a,a,a,b,c");
	}

	@Test
	public void testRoundRobinRule() {
		Map<String, AtomicInteger> selResult = new HashMap<>();
		List<TestServer> serverList = init();

		RoundRobinBalancer<TestServer> rule = new RoundRobinBalancer<>();
		for (TestServer server : serverList) {
			rule.addServer(server);
			selResult.put(server.getId(), new AtomicInteger(0));
		}

		for (int i = 0; i < 100; ++i) {
			TestServer sel = rule.choose();
			selResult.get(sel.getId()).incrementAndGet();
		}

		Assert.assertEquals(selResult.get("1").get(), 34);
		Assert.assertEquals(selResult.get("2").get(), 33);
		Assert.assertEquals(selResult.get("3").get(), 33);
	}

	@Test
	public void testLvsRoundRobinRule() {
		Map<String, AtomicInteger> selResult = new HashMap<>();
		List<TestServer> serverList = init();

		LvsRoundRobinBalancer<TestServer> rule = new LvsRoundRobinBalancer<>();
		for (TestServer server : serverList) {
			rule.addServer(server);
			selResult.put(server.getId(), new AtomicInteger(0));
		}

		for (int i = 0; i < 100; ++i) {
			TestServer sel = rule.choose();
			selResult.get(sel.getId()).incrementAndGet();
		}

		Assert.assertEquals(selResult.get("1").get(), 50);
		Assert.assertEquals(selResult.get("2").get(), 20);
		Assert.assertEquals(selResult.get("3").get(), 30);
	}
}
