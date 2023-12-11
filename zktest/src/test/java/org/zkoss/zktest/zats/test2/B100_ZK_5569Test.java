package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;
import org.zkoss.test.webdriver.ztl.Widget;

public class B100_ZK_5569Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		JQuery jqRadio = jq("@radio");
		check(jqRadio.eq(1));
		waitResponse();
		assertEquals("onCheck: Vegetables", getZKLog());
		assertNoAnyError();
		closeZKLog();
		check(jqRadio.eq(2));
		waitResponse();
		assertEquals("onCheck: Seafood", getZKLog());
		assertNoAnyError();
		closeZKLog();
		check(jqRadio.eq(3));
		waitResponse();
		assertEquals("onCheck: Fruits", getZKLog());
		assertNoAnyError();
		closeZKLog();
		check(jqRadio.eq(0));
		waitResponse();
		assertEquals("onCheck: All Food", getZKLog());
		assertNoAnyError();
		closeZKLog();
	}
}
