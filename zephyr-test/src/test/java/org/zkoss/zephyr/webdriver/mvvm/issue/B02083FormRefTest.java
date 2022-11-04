package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B02083FormRefTest extends WebDriverTestCase {
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

		assertEquals("AAA", tb1.val());
		assertEquals("AAA1", tb2.val());
		assertEquals("AAA2", tb3.val());
		assertEquals("AAA", lb1.text());
		assertEquals("AAA1", lb2.text());
		assertEquals("AAA2", lb3.text());

		type(tb1, "fu");
		waitResponse();
		assertEquals("fu", tb1.val());
		assertEquals("AAA1", tb2.val());
		assertEquals("AAA2", tb3.val());
		assertEquals("AAA", lb1.text());
		assertEquals("AAA1", lb2.text());
		assertEquals("AAA2", lb3.text());

		type(tb2, "fu1");
		waitResponse();
		assertEquals("fu", tb1.val());
		assertEquals("fu1", tb2.val());
		assertEquals("AAA2", tb3.val());
		assertEquals("AAA", lb1.text());
		assertEquals("AAA1", lb2.text());
		assertEquals("AAA2", lb3.text());

		type(tb3, "fu2");
		waitResponse();
		assertEquals("fu", tb1.val());
		assertEquals("fu1", tb2.val());
		assertEquals("fu2", tb3.val());
		assertEquals("AAA", lb1.text());
		assertEquals("AAA1", lb2.text());
		assertEquals("AAA2", lb3.text());

		click(save);
		waitResponse();
		assertEquals("fu", tb1.val());
		assertEquals("fu1", tb2.val());
		assertEquals("fu2", tb3.val());
		assertEquals("fu", lb1.text());
		assertEquals("fu1", lb2.text());
		assertEquals("fu2", lb3.text());
	}
}
