/* F50_3316591Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Apr 12 11:42:48 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class F50_3316591Test extends WebDriverTestCase {
	private static final int ARROW_PADDING = 8;

	@Test
	public void test() {
		connect();

		JQuery textbox = jq("@textbox");
		click(textbox);
		click(jq("body"));
		waitResponse();
		JQuery errorbox = jq("@errorbox");
		Assertions.assertTrue(errorbox.isVisible());

		click(jq("@radio:contains(Before_Start)"));
		waitResponse();
		checkBefore_(textbox, errorbox);
		check_Start(textbox, errorbox);

		click(jq("@radio:contains(Before_End)"));
		waitResponse();
		checkBefore_(textbox, errorbox);
		check_End(textbox, errorbox);

		click(jq("@radio:contains(End_Before)"));
		waitResponse();
		checkEnd_(textbox, errorbox);
		check_Before(textbox, errorbox);

		click(jq("@radio:contains(End_After)"));
		waitResponse();
		checkEnd_(textbox, errorbox);
		check_After(textbox, errorbox);

		click(jq("@radio:contains(After_End)"));
		waitResponse();
		checkAfter_(textbox, errorbox);
		check_End(textbox, errorbox);

		click(jq("@radio:contains(After_Start)"));
		waitResponse();
		checkAfter_(textbox, errorbox);
		check_Start(textbox, errorbox);

		click(jq("@radio:contains(Start_After)"));
		waitResponse();
		checkStart_(textbox, errorbox);
		check_After(textbox, errorbox);

		click(jq("@radio:contains(Start_Before)"));
		waitResponse();
		checkStart_(textbox, errorbox);
		check_Before(textbox, errorbox);

		click(jq("@radio:contains(Overlap):first"));
		waitResponse();
		Assertions.assertEquals(textbox.offsetTop(), errorbox.offsetTop(), 1);
		Assertions.assertEquals(textbox.offsetLeft(), errorbox.offsetLeft(), 1);

		click(jq("@radio:contains(Overlap_end)"));
		waitResponse();
		Assertions.assertEquals(textbox.offsetTop(), errorbox.offsetTop(), 1);
		Assertions.assertEquals(textbox.offsetLeft() + textbox.innerWidth(), errorbox.offsetLeft() + errorbox.innerWidth(), 1);

		click(jq("@radio:contains(Overlap_before)"));
		waitResponse();
		Assertions.assertEquals(textbox.offsetTop() + textbox.innerHeight() + ARROW_PADDING, errorbox.offsetTop() + errorbox.innerHeight(), 1);
		Assertions.assertEquals(textbox.offsetLeft(), errorbox.offsetLeft(), 1);

		click(jq("@radio:contains(Overlap_after)"));
		waitResponse();
		Assertions.assertEquals(textbox.offsetTop() + textbox.innerHeight() + ARROW_PADDING, errorbox.offsetTop() + errorbox.innerHeight(), 1);
		Assertions.assertEquals(textbox.offsetLeft() + textbox.innerWidth(), errorbox.offsetLeft() + errorbox.innerWidth(), 1);
	}

	private void checkBefore_(JQuery textbox, JQuery errorbox) {
		Assertions.assertEquals(textbox.offsetTop() - errorbox.innerHeight(), errorbox.offsetTop(), 1);
	}

	private void checkAfter_(JQuery textbox, JQuery errorbox) {
		Assertions.assertEquals(textbox.offsetTop() + textbox.innerHeight(), errorbox.offsetTop(), 1);
	}

	private void checkStart_(JQuery textbox, JQuery errorbox) {
		Assertions.assertEquals(textbox.offsetLeft() - errorbox.innerWidth(), errorbox.offsetLeft(), 1);
	}

	private void checkEnd_(JQuery textbox, JQuery errorbox) {
		Assertions.assertEquals(textbox.offsetLeft() + textbox.innerWidth(), errorbox.offsetLeft(), 1);
	}

	private void check_Before(JQuery textbox, JQuery errorbox) {
		Assertions.assertEquals(textbox.offsetTop(), errorbox.offsetTop(), 1);
	}

	private void check_After(JQuery textbox, JQuery errorbox) {
		Assertions.assertEquals(
				textbox.offsetTop() + textbox.innerHeight(),
				errorbox.offsetTop() + errorbox.innerHeight(), 1);
	}

	private void check_Start(JQuery textbox, JQuery errorbox) {
		Assertions.assertEquals(textbox.offsetLeft(), errorbox.offsetLeft(), 1);
	}

	private void check_End(JQuery textbox, JQuery errorbox) {
		Assertions.assertEquals(
				textbox.offsetLeft() + textbox.innerWidth(),
				errorbox.offsetLeft() + errorbox.innerWidth() , 1);
	}
}
