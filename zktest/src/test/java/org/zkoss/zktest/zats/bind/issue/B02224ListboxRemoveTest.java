package org.zkoss.zktest.zats.bind.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B02224ListboxRemoveTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		assertEquals(1, jq(".z-listheader-content:contains(col-0)").length());
		click(jq("@button:contains(widen model 100)"));
		click(jq("@button:contains(narrow model)"));
		sleep(5000);
		assertEquals(0, jq(".z-listheader-content:contains(col-0)").length());
	}
}
