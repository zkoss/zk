package org.zkoss.zephyr.webdriver.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author jameschu
 */
public class F85_ZK_3852Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		click(jq("@chosenbox"));
		waitResponse();
		click(jq(".z-chosenbox-option").eq(0));
		waitResponse();
		JQuery chosenboxInplace = jq(".z-chosenbox-inplace");
		assertTrue(chosenboxInplace.isVisible());
		assertEquals("data0", chosenboxInplace.text());
		assertEquals(0, jq(".z-chosenbox-item-content:visible").length());
		click(jq("@chosenbox"));
		waitResponse();
		click(jq(".z-chosenbox-option").eq(2));
		waitResponse();
		assertTrue(chosenboxInplace.isVisible());
		assertEquals("data0, data2", chosenboxInplace.text());
		assertEquals(0, jq(".z-chosenbox-item-content:visible").length());
		click(jq("@button").eq(1));
		waitResponse();
		assertFalse(chosenboxInplace.isVisible());
		assertEquals(2, jq(".z-chosenbox-item-content:visible").length());
		click(jq("@button").eq(0));
		waitResponse();
		assertTrue(chosenboxInplace.isVisible());
		assertEquals(0, jq(".z-chosenbox-item-content:visible").length());
	}
}