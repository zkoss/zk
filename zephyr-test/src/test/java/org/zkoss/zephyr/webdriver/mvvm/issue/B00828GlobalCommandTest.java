package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B00828GlobalCommandTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		JQuery post = jq("$post");
		JQuery msg = jq("$msg");
		for (int i = 0; i < 50; i++) {
			click(post);
			waitResponse();
			assertEquals(String.valueOf(i + 1), msg.text());
		}
		assertNoAnyError();
	}
}
