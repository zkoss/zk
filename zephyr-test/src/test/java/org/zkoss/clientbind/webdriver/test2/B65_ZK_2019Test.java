package org.zkoss.clientbind.webdriver.test2;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.clientbind.webdriver.ClientBindTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author jameschu
 */
public class B65_ZK_2019Test extends ClientBindTestCase {
	@Test
	public void test() {
		connect();
		sleep(2000);
		JQuery cb = jq(".z-chosenbox");
		waitResponse();
		sendKeys(jq(".z-chosenbox-input"), "oh");
		waitResponse(true);
		sleep(300);
		assertTrue(jq(".z-chosenbox-option:contains(john@company.org)").exists(), "Your will see 'John (john@company.org)' option.");
	}
}