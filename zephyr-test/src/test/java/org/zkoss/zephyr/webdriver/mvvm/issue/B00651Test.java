package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B00651Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		JQuery lb1 = jq("$lb1");
		JQuery intbox = jq("$intbox");
		JQuery doublebox = jq("$doublebox");
		assertEquals("Non-dirty", lb1.text());
		assertEquals("10", intbox.val());
		assertEquals("10.36", doublebox.val());

		type(doublebox, "36.01");
		waitResponse();
		assertEquals("Dirty", lb1.text());
		assertEquals("36.01", doublebox.val());
	}
}
