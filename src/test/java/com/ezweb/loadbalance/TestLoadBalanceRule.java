package com.ezweb.loadbalance;

import com.ezweb.engine.balance.Server;
import com.ezweb.engine.balance.rule.LvsRoundRobinRule;
import com.ezweb.engine.balance.rule.NginxRoundRobinRule;
import com.ezweb.engine.balance.rule.RoundRobinRule;
import com.google.common.collect.Lists;
import org.apache.logging.log4j.util.Strings;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
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

	List<TestServer> serverList = null;
	Map<String, AtomicInteger> selResult = null;

	@Before
	public void init() {
		serverList = Lists.newArrayList(
				new TestServer("1", 5),
				new TestServer("2", 2),
				new TestServer("3", 3)
		);

		selResult = new HashMap<>();
		for (int i = 0; i < serverList.size(); ++i) {
			selResult.put(serverList.get(i).getId(), new AtomicInteger(0));
		}
	}

	@After
	public void close() {
		this.serverList.clear();
		this.selResult.clear();
	}

	@Test
	public void testNginxRoundRobinRule() {
		NginxRoundRobinRule<TestServer> x = new NginxRoundRobinRule<>();
		for (TestServer server : serverList) {
			x.addServer(server);
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
		serverList = Lists.newArrayList(
				new TestServer("a", 5),
				new TestServer("b", 1),
				new TestServer("c", 1)
		);

		NginxRoundRobinRule<TestServer> x = new NginxRoundRobinRule<>();
		for (TestServer server : serverList) {
			x.addServer(server);
		}

		// 期望:>>>> (a, a, b, a, c, a, a)
		List<String> selIds = new ArrayList<>();
		for (int i = 0; i < 7; ++i) {
			TestServer sel = x.choose();
			selIds.add(sel.getId());
		}

		String selIdResult = Strings.join(selIds, ',');
		Assert.assertEquals(selIdResult, "a,a,b,a,c,a,a");
	}

	@Test
	public void testRoundRobinRule() {
		RoundRobinRule<TestServer> rule = new RoundRobinRule<>();
		for (TestServer server : serverList) {
			rule.addServer(server);
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
		LvsRoundRobinRule<TestServer> rule = new LvsRoundRobinRule<>();
		for (TestServer server : serverList) {
			rule.addServer(server);
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
