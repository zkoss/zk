package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

public class B96_ZK_5252Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		// Click somewhere outside the modal.
		click(jq(".z-modal-mask"));
		waitResponse();
		// Make sure that the text box regains focus.
		Assert.assertEquals("true", getEval("jq('.z-textbox')[0] === document.activeElement"));
	}
}
