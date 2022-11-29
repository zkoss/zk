package org.zkoss.zephyr.webdriver.test2;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.Widget;

public class B70_ZK_2217Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		assertNoAnyError();
		click(jq(".z-button"));
		waitResponse();
		Widget lb = jq(".z-listbox").toWidget();
		assertTrue(jq(lb.$n("foot")).isVisible() && jq(lb.$n("pgib")).isVisible(), "the footer and paging should not hide");
	}
}
