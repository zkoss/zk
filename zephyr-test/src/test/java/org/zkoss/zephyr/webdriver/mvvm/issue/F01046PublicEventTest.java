package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class F01046PublicEventTest extends WebDriverTestCase {

	@Test
	public void test() {
		connect();

		JQuery msg1 = jq("$win1 $msg1");
		JQuery btn1 = jq("$win1 $btn1");
		JQuery msg2 = jq("$win2 $msg2");
		JQuery btn2 = jq("$win2 $btn2");

		click(btn1);
		waitResponse();
		assertEquals("Hello i am a vm", msg2.text());

		click(btn2);
		waitResponse();
		assertEquals("Hello i am a composer", msg1.text());
	}
}
