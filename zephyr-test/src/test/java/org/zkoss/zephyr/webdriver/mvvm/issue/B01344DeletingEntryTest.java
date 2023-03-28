package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B01344DeletingEntryTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		for (int i = 10; i > 0; i--) {
			assertEquals(i + "", jq("$lb1").text());
			click(jq("$btn1"));
			waitResponse();
		}
		assertEquals("0", jq("$lb1").text());
	}
}
