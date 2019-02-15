package org.zkoss.zktest.zats.test2;

import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

import static junit.framework.TestCase.assertTrue;

public class B70_ZK_2616Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq("@button"));
		for (int i = 0; i <= 5; i++) {
			click(jq("@button").eq(1));
		}
		sleep(2000);
		waitResponse();
		JQuery window = jq(".z-window-header");
		assertTrue(!window.exists() || window.text().equals("Session Timeout"));
	}
}