package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B96_ZK_5252Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		// Click somewhere outside the modal.
		click(jq(".z-modal-mask"));
		waitResponse();
		// Make sure that the text box regains focus.
		assertEquals("true", getEval("jq('.z-textbox')[0] === document.activeElement"));
	}
}
