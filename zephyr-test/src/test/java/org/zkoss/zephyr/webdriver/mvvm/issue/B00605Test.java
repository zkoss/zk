package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B00605Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery tb1 = jq("$tb1");
		JQuery lb1 = jq("$lb1");
		JQuery tb2 = jq("$inc1").find("@textbox"); //can't use $tb2???
		JQuery lb2 = jq("$inc1").find("@label"); //can't use $lb2???
		assertEquals("A", tb1.val());
		assertEquals("A", lb1.text());
		assertEquals("A", tb2.val());
		assertEquals("A", lb2.text());

		type(tb1, "JJ");
		waitResponse();
		assertEquals("JJ", tb1.val());
		assertEquals("JJ", lb1.text());
		assertEquals("A", tb2.val());
		assertEquals("A", lb2.text());

		type(tb2, "KK");
		waitResponse();
		assertEquals("JJ", tb1.val());
		assertEquals("JJ", lb1.text());
		assertEquals("KK", tb2.val());
		assertEquals("KK", lb2.text());

		JQuery btn1 = jq("$btn1");
		click(btn1);
		waitResponse();
		JQuery tb3 = jq("$inc2").find("@textbox"); //can't use $tb2???
		JQuery lb3 = jq("$inc2").find("@label"); //can't use $lb2???
		assertEquals("A", tb3.val());
		assertEquals("A", lb3.text());

		type(tb3, "LL");
		waitResponse();
		assertEquals("JJ", tb1.val());
		assertEquals("JJ", lb1.text());
		assertEquals("KK", tb2.val());
		assertEquals("KK", lb2.text());
		assertEquals("LL", tb3.val());
		assertEquals("LL", lb3.text());

		//test again since inc2 appears
		type(tb1, "X");
		waitResponse();
		assertEquals("X", tb1.val());
		assertEquals("X", lb1.text());
		assertEquals("KK", tb2.val());
		assertEquals("KK", lb2.text());
		assertEquals("LL", tb3.val());
		assertEquals("LL", lb3.text());

		type(tb2, "Y");
		waitResponse();
		assertEquals("X", tb1.val());
		assertEquals("X", lb1.text());
		assertEquals("Y", tb2.val());
		assertEquals("Y", lb2.text());
		assertEquals("LL", tb3.val());
		assertEquals("LL", lb3.text());

		type(tb3, "Z");
		waitResponse();
		assertEquals("X", tb1.val());
		assertEquals("X", lb1.text());
		assertEquals("Y", tb2.val());
		assertEquals("Y", lb2.text());
		assertEquals("Z", tb3.val());
		assertEquals("Z", lb3.text());
	}
}
