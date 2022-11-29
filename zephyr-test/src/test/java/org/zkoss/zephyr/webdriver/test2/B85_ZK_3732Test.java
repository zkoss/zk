package org.zkoss.zephyr.webdriver.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author bob peng
 */
public class B85_ZK_3732Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		assertEquals(1, jq("@listbox").length());
		JQuery btn = jq("$btn");
		click(btn);
		waitResponse();
		assertEquals(0, jq("@listbox").length());
		assertFalse(jq(".z-messagebox-error").exists(), "error popped");
		assertNoAnyError();
	}
}