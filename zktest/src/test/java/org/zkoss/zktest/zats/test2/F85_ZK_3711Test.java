package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;
import org.zkoss.test.webdriver.ztl.Widget;

public class F85_ZK_3711Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		JQuery btns = jq("@button");
		click(btns.eq(1)); // push page 2
		waitResponse();
		click(btns.eq(3)); // push page 4
		waitResponse();
		click(btns.eq(7)); // replace page 3
		waitResponse();

		Widget tabbox = jq("@tabbox").toWidget();
		navigatePage(false);
		waitResponse();
		assertEquals(tabbox.get("selectedIndex"), "1"); // page 2
		navigatePage(true);
		waitResponse();
		assertEquals(tabbox.get("selectedIndex"), "2"); // page 3
	}

	public void navigatePage(boolean forword) {
		if (forword) {
			getWebDriver().navigate().forward();
		} else {
			getWebDriver().navigate().back();
		}
	}
}
