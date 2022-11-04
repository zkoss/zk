package org.zkoss.zephyr.webdriver.mvvm.issue;


import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B00993IncludeReloadTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery l1 = jq("$win2 $l1");
		JQuery l2 = jq("$win2 $l2");
		JQuery reload = jq("$reload");

		String val1;
		String val2;

		val1 = l1.text();
		val2 = l2.text();
		click(reload);
		waitResponse();
		l1 = jq("$win2 $l1");
		l2 = jq("$win2 $l2");
		assertNotEquals(val1, l1.text());
		assertNotEquals(val2, l2.text());

		try {
			Thread.sleep(100);
		} catch (Exception e) {

		}
		val1 = l1.text();
		val2 = l2.text();
		click(reload);
		waitResponse();
		l1 = jq("$win2 $l1");
		l2 = jq("$win2 $l2");
		assertNotEquals(val1, l1.text());
		assertNotEquals(val2, l2.text());

		try {
			Thread.sleep(100);
		} catch (Exception e) {

		}
		val1 = l1.text();
		val2 = l2.text();
		click(reload);
		waitResponse();
		l1 = jq("$win2 $l1");
		l2 = jq("$win2 $l2");
		assertNotEquals(val1, l1.text());
		assertNotEquals(val2, l2.text());

		try {
			Thread.sleep(100);
		} catch (Exception e) {

		}
		val1 = l1.text();
		val2 = l2.text();
		click(reload);
		waitResponse();
		l1 = jq("$win2 $l1");
		l2 = jq("$win2 $l2");
		assertNotEquals(val1, l1.text());
		assertNotEquals(val2, l2.text());

		try {
			Thread.sleep(100);
		} catch (Exception e) {

		}
		val1 = l1.text();
		val2 = l2.text();
		click(reload);
		waitResponse();
		l1 = jq("$win2 $l1");
		l2 = jq("$win2 $l2");
		assertNotEquals(val1, l1.text());
		assertNotEquals(val2, l2.text());
	}
}
