package org.zkoss.zephyr.webdriver.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B60_ZK_919Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		assertEquals(jq(".z-datebox-input").eq(0).val().split(",")[2], jq(".z-datebox-input").eq(1).val().split(",")[2]);
		assertEquals(jq(".z-datebox-input").eq(1).val().split(",")[2], jq(".z-datebox-input").eq(2).val().split(",")[2]);
		assertEquals(jq(".z-datebox-input").eq(2).val().split(",")[2], jq(".z-datebox-input").eq(3).val().split(",")[2]);
		for (int i = 0; i < 4; i++) {
			click(jq(".z-datebox:eq(" + i + ")").find(".z-datebox-button"));
			waitResponse();
			click(jq(".z-calendar:eq(" + i + ") .z-calendar-cell:contains(14)"));
			waitResponse();
		}
		assertFalse(jq(".z-errorbox").exists(), "should not see any error message.");
	}
}