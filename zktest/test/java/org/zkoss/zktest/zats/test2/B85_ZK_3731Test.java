package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

public class B85_ZK_3731Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		for (int i = 0; i < 9; i++) {
			click(jq("a").get(i));
			waitResponse();
		}
		assertEquals(10, driver.getWindowHandles().size());
	}
}
