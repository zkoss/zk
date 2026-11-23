package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

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
