package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author jameschu
 */
public class B30_2446682Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq("$testButton"));
		waitResponse();
		assertTrue(jq(".z-window-modal").exists());
		assertEquals("Illegal event method name(component id not specified or consecutive '$'): onClick$$testButton", jq(".z-messagebox > span.z-label").text());
	}
}
