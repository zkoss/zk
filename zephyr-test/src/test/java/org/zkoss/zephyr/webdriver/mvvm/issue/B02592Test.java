package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B02592Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		//the following five are labels
		JQuery intboxes = jq("@intbox");
		JQuery textbox = jq("@textbox");
		JQuery button = jq("@button");
		JQuery err1 = jq("$err1");
		JQuery err2 = jq("$err2");
		JQuery err3 = jq("$err3");
		assertEquals("30", intboxes.eq(0).val());
		assertEquals("0", intboxes.eq(1).val());
		assertEquals("Peter", textbox.val());
		click(button);
		waitResponse();
		assertEquals("", err1.text());
		assertEquals("", err2.text());
		assertEquals("", err3.text());
		type(intboxes.eq(0), "300");
		type(intboxes.eq(1), "300");
		type(textbox, "a2");
		waitResponse();
		click(button);
		waitResponse();

		assertEquals("must be less than or equal to 120", err1.text());
		assertEquals("size must be between 4 and 10", err2.text());
		assertEquals("", err3.text());
	}
}
