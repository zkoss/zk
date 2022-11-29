package org.zkoss.zephyr.webdriver.test2;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author jameschu
 */
public class B65_ZK_2019Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		sendKeys(jq(".z-chosenbox-input"), "oh");
		waitResponse(true);
		sleep(300);
		assertTrue(jq(".z-chosenbox-option:contains(john@company.org)").isVisible(), "Your will see 'John (john@company.org)' option.");
	}
}