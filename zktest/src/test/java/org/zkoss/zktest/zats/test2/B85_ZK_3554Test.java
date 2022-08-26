package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author bob peng
 */
public class B85_ZK_3554Test extends WebDriverTestCase {
	@Test
	public void test1() {
		connect();
		rightClick(jq("$btn_context"));
		waitResponse();
		assertTrue(jq(".z-menupopup-content").exists(),
				"error: did not show menupopup when right click button");
	}

	@Test
	public void test2() {
		connect();
		click(jq("$btn_popupId"));
		waitResponse();
		assertTrue(jq(".z-menupopup-content").exists(),
				"error: did not show menupopup when left click button");
	}
}