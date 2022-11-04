package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B01908DatabindingOnPagingTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery pg = jq("$pg");
		JQuery lab1 = jq("$lab1");
		assertEquals(3, Integer.parseInt(pg.toWidget().get("activePage")));
		assertEquals(10, Integer.parseInt(pg.toWidget().get("pageSize")));
		assertEquals(100, Integer.parseInt(pg.toWidget().get("totalSize")));
		assertEquals("3", lab1.text());
	}
}
