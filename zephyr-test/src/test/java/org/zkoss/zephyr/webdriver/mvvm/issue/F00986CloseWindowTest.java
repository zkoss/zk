package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class F00986CloseWindowTest extends WebDriverTestCase {

	@Test
	public void test() {
		connect();

		JQuery detach = jq("$detach");
		JQuery win1 = jq("$win1");
		JQuery win2 = jq("$win2");
		JQuery win3 = jq("$win3");
		JQuery win4 = jq("$win4");
		assertTrue(win1.length() != 0);
		assertTrue(win2.length() != 0);
		assertTrue(win3.length() != 0);
		assertFalse(win4.length() != 0);

		click(detach);
		waitResponse();
		win1 = jq("$win1");
		win2 = jq("$win2");
		win3 = jq("$win3");
		win4 = jq("$win4");
		assertFalse(win1.length() != 0);
		assertTrue(win2.length() != 0);
		assertFalse(win3.length() != 0);
		assertFalse(win4.length() != 0);
	}
}
