package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author jameschu
 */
public class B30_1823213Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		JQuery lbs = jq("@listbox");
		assertTrue(lbs.eq(0).parent().innerWidth() >= 500);
		assertTrue(lbs.eq(1).parent().innerWidth() >= 500);
	}
}
