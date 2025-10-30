package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B80_ZK_3077Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		clickAll();
		clickAll();
		assertTrue(jq(".z-listitem-focus").length() == 1);
		assertEquals(jq(".z-listitem").get(3).toElement().get("id"), jq(".z-listitem-focus").toElement().get("id"));
	}

	private void clickAll() {
		click(jq(".z-listitem-checkbox").get(0));
		waitResponse();
		click(jq(".z-listitem-checkbox").get(1));
		waitResponse();
		click(jq(".z-listitem-checkbox").get(2));
		waitResponse();
		click(jq(".z-listitem-checkbox").get(3));
		waitResponse();
	}
}
