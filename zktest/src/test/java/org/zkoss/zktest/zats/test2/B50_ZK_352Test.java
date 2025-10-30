package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B50_ZK_352Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		JQuery lb = jq("@listbox");
		JQuery body = lb.find(".z-listbox-body");
		body.scrollLeft(500);
		waitResponse();
		assertTrue(parseInt(body.attr("scrollLeft")) < 110);
		assertTrue(0 >= jq("@listheader").eq(2).width());
		assertTrue(0 >= jq("@listheader").eq(3).width());
	}
}
