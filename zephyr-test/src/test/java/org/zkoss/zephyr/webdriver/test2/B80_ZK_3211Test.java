package org.zkoss.zephyr.webdriver.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import org.zkoss.zephyr.webdriver.ClientBindTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

@Disabled
public class B80_ZK_3211Test extends ClientBindTestCase {

	@Test
	public void test() {
		connect();
		JQuery r0 = jq("$result0");
		JQuery r1 = jq("$result1");
		JQuery r2 = jq("$result2");
		JQuery btn0 = jq("$btn0");
		JQuery btn1 = jq("$btn1");
		JQuery btn2 = jq("$btn2");
		JQuery move = jq("$move");
		click(btn0);
		waitResponse();
		assertEquals("my label.", r0.text());
		click(btn1);
		waitResponse();
		assertEquals("my label1.", r1.text());
		click(btn2);
		waitResponse();
		assertEquals("my label2.", r2.text());
		waitResponse();
		click(move);
		waitResponse();
		click(btn0);
		waitResponse();
		assertEquals("my label..", r0.text());
		click(btn1);
		waitResponse();
		assertEquals("my label1..", r1.text());
		click(btn2);
		waitResponse();
		assertEquals("my label2..", r2.text());
	}
}
