package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B86_ZK_4248Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		jq(".z-listbox-body").scrollTop(1000);
		waitResponse();
		Assertions.assertFalse(isZKLogAvailable());
	}
}
