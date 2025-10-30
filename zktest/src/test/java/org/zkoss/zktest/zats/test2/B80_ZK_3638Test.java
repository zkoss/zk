package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B80_ZK_3638Test extends WebDriverTestCase{

	@Test
	public void test () {
		connect();
		JQuery btn = jq("@button");
		click(btn.eq(0));
		waitResponse();
		assertEquals(true, isZKLogAvailable());
		assertEquals(true, getZKLog().contains("onCustomzkevent"));
	}
}
