package org.zkoss.zephyr.webdriver.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriverException;

import org.zkoss.zephyr.webdriver.ClientBindTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B70_ZK_2616Test extends ClientBindTestCase {
	@Test
	public void test() {
		connect();
		click(jq("@button"));
		try {
			for (int i = 0; i <= 5; i++) {
				click(jq("@button").eq(1));
			}
		} catch (WebDriverException ignored) {
			// expected if a redirection is performing
		}
		sleep(2000);
		waitResponse();
		JQuery window = jq(".z-window-header");
		Assertions.assertTrue(!window.exists() || window.text().equals("Session Timeout"));
	}
}
