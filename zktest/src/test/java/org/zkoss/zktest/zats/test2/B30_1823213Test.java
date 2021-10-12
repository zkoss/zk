package org.zkoss.zktest.zats.test2;

import static junit.framework.TestCase.assertTrue;

import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

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
