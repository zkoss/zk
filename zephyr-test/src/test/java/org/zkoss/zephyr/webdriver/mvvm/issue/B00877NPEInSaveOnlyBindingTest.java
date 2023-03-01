package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B00877NPEInSaveOnlyBindingTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		JQuery msg = jq("$msg");
		JQuery tb = jq("$tb");

		type(tb, "abc");
		waitResponse();
		assertTrue(hasError());

		type(tb, "Lin");
		waitResponse();
		assertNoAnyError();
		assertEquals("Lin", msg.text());
	}
}
