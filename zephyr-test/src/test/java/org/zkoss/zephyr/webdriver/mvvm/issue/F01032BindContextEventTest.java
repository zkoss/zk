package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class F01032BindContextEventTest extends WebDriverTestCase {

	@Test
	public void test() {
		connect();

		JQuery msg = jq("$msg");
		JQuery tb = jq("$tb");
		JQuery btn = jq("$btn");

		type(tb, "a");
		waitResponse();
		assertEquals("evt1:onChange,evt2:onChange, cmd:cmd", msg.text());

		click(btn);
		waitResponse();
		assertEquals("evt1:onClick,evt2:onClick, cmd:cmd", msg.text());
	}
}
