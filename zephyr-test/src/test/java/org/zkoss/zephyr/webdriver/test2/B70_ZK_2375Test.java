package org.zkoss.zephyr.webdriver.test2;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B70_ZK_2375Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		JQuery icons = jq(".z-tree-icon");
		click(icons.eq(0));
		waitResponse();
		click(icons.eq(1));
		waitResponse();
		click(icons.eq(2));
		waitResponse();
		click(icons.eq(3));
		waitResponse();
		click(icons.eq(4));
		waitResponse();
		click(icons.eq(5));
		waitResponse();
		click(icons.eq(6));
		waitResponse();
		click(icons.eq(7));
		waitResponse();
		click(jq(".z-paging-next"));
		waitResponse();
		click(jq(".z-paging-previous"));
		waitResponse();
		click(icons.eq(7));
		waitResponse();
		assertTrue(jq("span.z-treecell-text:contains(8)").exists(), "node 8 should exist.");
	}
}
