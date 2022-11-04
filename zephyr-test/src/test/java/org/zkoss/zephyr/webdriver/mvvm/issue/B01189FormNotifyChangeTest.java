package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B01189FormNotifyChangeTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery tb1 = jq("$tb1");
		JQuery tb2 = jq("$tb2");
		JQuery tb3 = jq("$tb3");
		JQuery tb4 = jq("$tb4");
		JQuery save1 = jq("$save1");
		JQuery lb1 = jq("$lb1");

		assertEquals("", tb1.val());
		assertEquals("", tb2.val());
		assertEquals("", tb3.val());
		assertEquals("", tb4.val());
		assertEquals("", lb1.text());

		type(tb1, "A");
		waitResponse();
		assertEquals("A", tb2.val());
		assertEquals("A", tb3.val());
		assertEquals("A", tb4.val());
		click(save1);
		waitResponse();
		assertEquals("A", lb1.text());

		type(tb2, "B");
		waitResponse();
		assertEquals("B", tb1.val());
		assertEquals("B", tb3.val());
		assertEquals("B", tb4.val());
		click(save1);
		waitResponse();
		assertEquals("B", lb1.text());

		type(tb3, "C");
		waitResponse();
		assertEquals("C", tb1.val());
		assertEquals("C", tb2.val());
		assertEquals("C", tb4.val());
		click(save1);
		waitResponse();
		assertEquals("C", lb1.text());

		type(tb4, "D");
		waitResponse();
		assertEquals("D", tb1.val());
		assertEquals("D", tb2.val());
		assertEquals("D", tb3.val());
		click(save1);
		waitResponse();
		assertEquals("D", lb1.text());

		type(tb1, "E");
		waitResponse();
		assertEquals("E", tb2.val());
		assertEquals("E", tb3.val());
		assertEquals("E", tb4.val());
		click(save1);
		waitResponse();
		assertEquals("E", lb1.text());

		type(tb2, "F");
		waitResponse();
		assertEquals("F", tb1.val());
		assertEquals("F", tb3.val());
		assertEquals("F", tb4.val());
		click(save1);
		waitResponse();
		assertEquals("F", lb1.text());

		type(tb3, "G");
		waitResponse();
		assertEquals("G", tb1.val());
		assertEquals("G", tb2.val());
		assertEquals("G", tb4.val());
		click(save1);
		waitResponse();
		assertEquals("G", lb1.text());

		type(tb4, "H");
		waitResponse();
		assertEquals("H", tb1.val());
		assertEquals("H", tb2.val());
		assertEquals("H", tb3.val());
		click(save1);
		waitResponse();
		assertEquals("H", lb1.text());

		JQuery tb5 = jq("$tb5");
		JQuery tb6 = jq("$tb6");
		JQuery tb7 = jq("$tb7");
		JQuery tb8 = jq("$tb8");
		JQuery save2 = jq("$save2");
		JQuery lb2 = jq("$lb2");
		JQuery lb3 = jq("$lb3");

		assertEquals("", tb5.val());
		assertEquals("", tb6.val());
		assertEquals("", tb7.val());
		assertEquals("", tb8.val());
		assertEquals("", lb2.text());
		assertEquals("", lb3.text());

		type(tb5, "A");
		waitResponse();
		assertEquals("A", tb6.val());
		assertEquals("", tb7.val());
		assertEquals("", tb8.val());

		type(tb6, "B");
		waitResponse();
		assertEquals("B", tb5.val());
		assertEquals("", tb7.val());
		assertEquals("", tb8.val());

		type(tb7, "C");
		waitResponse();
		assertEquals("B", tb5.val());
		assertEquals("B", tb6.val());
		assertEquals("C", tb8.val());

		type(tb8, "D");
		waitResponse();
		assertEquals("B", tb5.val());
		assertEquals("B", tb6.val());
		assertEquals("D", tb7.val());

		click(save2);
		waitResponse();
		assertEquals("B", lb2.text());
		assertEquals("D", lb3.text());
	}
}
