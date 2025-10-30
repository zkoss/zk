package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B70_ZK_2636Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		JQuery show = jq("@button").eq(0);
		JQuery save = jq("@button").eq(1);
		click(jq(".z-combobox-button"));
		waitResponse();
		click(jq(".z-combobox-popup ul li"));
		waitResponse();
		click(show);
		waitResponse();
		click(save);
		sleep(1000);
		assertEquals("a\nValidate value: a", getZKLog());
	}
}
