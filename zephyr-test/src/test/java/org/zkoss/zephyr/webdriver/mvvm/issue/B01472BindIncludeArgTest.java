package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B01472BindIncludeArgTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();

		JQuery lb1 = jq("$win1 $lb1");
		JQuery lb2 = jq("$win1 $inc1 $win2 $lb2");
		JQuery tb1 = jq("$win1 $tb1");
		JQuery btn1 = jq("$win1 $btn1");

		assertEquals("ABC", lb1.text());
		assertEquals("ABC", lb2.text());

		type(tb1, "XYZ");
		waitResponse();
		click(btn1);
		waitResponse();
		lb2 = jq("$win1 $inc1 $win2 $lb2");
		assertEquals("XYZ", lb1.text());
		assertEquals("XYZ", lb2.text());
	}
}
