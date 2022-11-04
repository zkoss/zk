package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B01157CorrectConverterNameTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery lb1 = jq("$lb1");
		JQuery lb2 = jq("$lb2");

		assertEquals("12,345.68", lb1.text());
		String[] date = lb2.text().split("/");
		assertEquals(3, date.length);
		assertEquals(4, date[0].length());
		assertEquals(2, date[1].length());
		assertEquals(2, date[2].length());


	}
}
