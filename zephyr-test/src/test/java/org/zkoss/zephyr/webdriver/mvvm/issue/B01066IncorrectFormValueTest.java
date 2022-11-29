package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B01066IncorrectFormValueTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery tb1 = jq("$tb1");
		JQuery save = jq("$save");
		JQuery lb1 = jq("$lb1");
		JQuery lb2 = jq("$lb2");

		assertEquals("A", lb1.text());
		assertEquals("A", lb2.text());

		type(tb1, "Abc");
		waitResponse();
		assertEquals("A", lb1.text());
		assertEquals("A", lb2.text());

		click(save);
		waitResponse();
		assertEquals("Abc", lb1.text());
		assertEquals("Abc", lb2.text());
		assertNoAnyError();
	}
}
