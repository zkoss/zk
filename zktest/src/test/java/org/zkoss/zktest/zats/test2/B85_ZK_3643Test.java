package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B85_ZK_3643Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery btn = jq("@button");
		click(btn.eq(0));
		waitResponse();
		click(btn.eq(0));
		waitResponse();

		JQuery listheaders = jq("th.z-listheader");
		JQuery hdfakers = jq("col[id$=hdfaker]");
		int listheadersLength = listheaders.length();
		int hdfakersLength = hdfakers.length();
		assertTrue(listheadersLength > 0, "List header count must > 0");
		assertEquals(listheadersLength, hdfakersLength, "<th> and <col> get out of synch");
	}
}
