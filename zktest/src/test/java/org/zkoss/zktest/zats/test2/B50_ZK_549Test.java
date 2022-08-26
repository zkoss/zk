package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B50_ZK_549Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		JQuery gbs = jq("@groupbox");
		for (int i = 0; i < 12; i++) {
			JQuery gb = gbs.eq(i);
			assertEquals(jq(gb).outerHeight(), 200, "Height should be 200px");
			JQuery header = gb.find(".z-groupbox-title");
			if (!header.exists()) {
				header = jq(gb).find(".z-caption").eq(0);
			}
			if (header.exists()) {
				click(header);
				waitResponse();
				click(header);
				waitResponse();
				assertEquals(jq(gb).outerHeight(), 200, "Height should be 200px");
			}
		}
	}
}
