package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @see org.zkoss.zktest.zats.test2.B96_ZK_5252Test
 */
public class B96_ZK_5258Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		// Open the modal.
		click(jq("@button"));
		waitResponse();
		// Make sure that the text box regains focus.
		assertEquals("true", getEval("jq('.z-textbox')[0] === document.activeElement"));
	}
}
