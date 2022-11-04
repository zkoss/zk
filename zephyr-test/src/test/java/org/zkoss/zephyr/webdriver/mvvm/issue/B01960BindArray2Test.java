package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B01960BindArray2Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery grid = jq("$grid");
		JQuery rows = grid.find("@row");
		assertEquals(4, rows.length());

		JQuery tb1 = rows.eq(0).find("@textbox");
		JQuery lb1 = rows.eq(0).find("@label");
		JQuery tb2 = rows.eq(1).find("@textbox");
		JQuery lb2 = rows.eq(1).find("@label");
		JQuery tb3 = rows.eq(2).find("@textbox");
		JQuery lb3 = rows.eq(2).find("@label");
		JQuery tb4 = rows.eq(3).find("@textbox");
		JQuery lb4 = rows.eq(3).find("@label");

		assertEquals("This", lb1.text());
		assertEquals("is", lb2.text());
		assertEquals("a", lb3.text());
		assertEquals("Test", lb4.text());

		type(tb1, "yo"); //the original row will be removed and attach with a new one!!!!
		waitResponse();
		rows = grid.find("@row");
		lb1 = rows.eq(0).find("@label");
		assertEquals("yo", lb1.text());

		type(tb2, "yoo");
		waitResponse();
		rows = grid.find("@row");
		lb2 = rows.eq(1).find("@label");
		assertEquals("yoo", lb2.text());

		type(tb3, "yooo");
		waitResponse();
		rows = grid.find("@row");
		lb3 = rows.eq(2).find("@label");
		assertEquals("yooo", lb3.text());

		type(tb4, "yoooo");
		waitResponse();
		rows = grid.find("@row");
		lb4 = rows.eq(3).find("@label");
		assertEquals("yoooo", lb4.text());
	}
}