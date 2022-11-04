package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B00878WrongValueExceptionTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery msgName = jq("$msgName");
		JQuery msgAge = jq("$msgAge");
		JQuery msgScore = jq("$msgScore");
		JQuery inpName = jq("$inpName");
		JQuery inpAge = jq("$inpAge");
		JQuery inpScore = jq("$inpScore");
		JQuery save = jq("$save");

		assertEquals("", msgName.text());
		assertEquals("0", msgAge.text());
		assertEquals("0", msgScore.text());

		type(inpName, "Chen");
		waitResponse();
		type(inpAge, "3");
		waitResponse();
		type(inpScore, "-1");
		waitResponse();
		click(save);
		waitResponse();
		assertEquals("", msgName.text());
		assertEquals("0", msgAge.text());
		assertEquals("0", msgScore.text());
		//should test error msg, but not support

		type(inpName, "Lin");
		waitResponse();
		type(inpAge, "5");
		waitResponse();
		type(inpScore, "-2");
		waitResponse();
		click(save);
		waitResponse();
		assertEquals("", msgName.text());
		assertEquals("0", msgAge.text());
		assertEquals("0", msgScore.text());
		//should test error msg, but not support

		type(inpName, "Lin");
		waitResponse();
		type(inpAge, "24");
		waitResponse();
		type(inpScore, "-3");
		waitResponse();
		click(save);
		waitResponse();
		assertEquals("", msgName.text());
		assertEquals("0", msgAge.text());
		assertEquals("0", msgScore.text());
		//should test error msg, but not support

		type(inpName, "Lin");
		waitResponse();
		type(inpAge, "24");
		waitResponse();
		type(inpScore, "34");
		waitResponse();
		click(save);
		waitResponse();
		assertEquals("Lin", msgName.text());
		assertEquals("24", msgAge.text());
		assertEquals("34", msgScore.text());
		//should test error msg, but not support
		
		/*
		var errorPopup = jq(".z-errorbox")
		
		verifyEquals(3, errorPopup.length())
		
		verifyEquals(2, errorPopup.length())
		
		verifyEquals(1, errorPopup.length())
		
		verifyEquals(0, errorPopup.length())*/
	}
}
