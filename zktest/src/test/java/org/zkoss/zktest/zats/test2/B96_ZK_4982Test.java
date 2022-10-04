package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Test;
import org.zkoss.test.webdriver.WebDriverTestCase;

public class B96_ZK_4982Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq("$selectButton"));
		waitResponse();
		assertNoAnyError(); // No error reported in console, nor ZK error popup.
	}
}
