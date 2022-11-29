package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B01347CheckboxCrashTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		JQuery listbox = jq("@listbox");
		assertTrue(listbox.length() > 0);
		click(jq("@checkbox"));
		waitResponse();
		assertNoAnyError();
	}
}
