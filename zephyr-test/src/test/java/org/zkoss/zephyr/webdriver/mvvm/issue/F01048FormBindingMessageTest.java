package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class F01048FormBindingMessageTest extends WebDriverTestCase {

	@Test
	public void test() {
		connect();

		JQuery tb1 = jq("$tb1");
		JQuery tb2 = jq("$tb2");
		JQuery tb3 = jq("$tb3");
		JQuery lb1 = jq("$lb1");
		JQuery lb2 = jq("$lb2");
		JQuery lb3 = jq("$lb3");
		JQuery save = jq("$save");

		click(save);
		waitResponse();
		assertEquals("First name is missing.", lb1.text());
		assertEquals("Last name is missing.", lb2.text());
		assertEquals("Age is missing.", lb3.text());

		type(tb1, "Dennis");
		waitResponse();
		assertEquals("", lb1.text());

		type(tb2, "Chen");
		waitResponse();
		assertEquals("", lb2.text());

		type(tb3, "35");
		waitResponse();
		assertEquals("", lb3.text());

		type(tb1, "");
		waitResponse();
		type(tb3, "");
		waitResponse();
		assertEquals("", lb1.text());
		assertEquals("", lb2.text());
		assertEquals("", lb3.text());

		click(save);
		waitResponse();
		assertEquals("First name is missing.", lb1.text());
		assertEquals("", lb2.text());
		assertEquals("Age is missing.", lb3.text());

		type(tb1, "DennisA");
		waitResponse();
		assertEquals("", lb1.text());

		type(tb2, "ChenB");
		waitResponse();
		assertEquals("", lb2.text());

		type(tb3, "37");
		waitResponse();
		assertEquals("", lb3.text());

		click(save);
		waitResponse();
		assertEquals("", lb1.text());
		assertEquals("", lb2.text());
		assertEquals("", lb3.text());
		assertEquals("Update DennisA,ChenB,37", jq("$msg").text());
	}
}
