package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B96_ZK_5216Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		JQuery jqDateboxInput = jq(".z-datebox-input");
		assertTrue(jqDateboxInput.val().endsWith("p. m."));
		click(jq(".z-datebox-button"));
		waitResponse();
		click(jq(".z-timebox-up"));
		waitResponse();
		assertTrue(jqDateboxInput.val().endsWith("a. m."));
	}
}
