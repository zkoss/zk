package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class F00633Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();

		JQuery l11 = jq("$l11");
		JQuery l12 = jq("$l12");
		assertEquals("onCreate 1", l11.text());
		assertEquals("onCreate 2", l12.text());

		click(jq("$btn1"));
		waitResponse();
		assertEquals("doCommand1", l11.text());

		click(jq("$btn2"));
		waitResponse();
		assertEquals("doCommand2", l11.text());

		click(jq("$btn3"));
		waitResponse();
		assertEquals("doCommand3 btn3 true", l11.text());

		click(jq("$btn4"));
		waitResponse();
		assertEquals("doCommand4 3 false null btn4 true", l11.text());

		click(jq("$btn5"));
		waitResponse();
		assertEquals("doCommand5 99 true XYZ btn5 true", l11.text());

		click(jq("$btn6"));
		waitResponse();
		assertEquals("doCommand6 9 true ABCD btn6 true", l11.text());

		click(jq("$btn7"));
		waitResponse();
		assertEquals("doCommandX 9 true XYZ cmd7", l11.text());

		click(jq("$btn8"));
		waitResponse();
		assertEquals("doCommandX 22 true ABCD cmd8", l11.text());

		click(jq("$btn9"));
		waitResponse();
		assertEquals("doCommandX 9 false EFG cmd9", l11.text());

		click(jq("$btn10"));
		waitResponse();
		assertEquals("object is btn10", l12.text());

		click(jq("$btn11"));
		waitResponse();
		assertEquals("object is desktop", l12.text());

		click(jq("$btn12"));
		waitResponse();
		assertEquals("object is h11", l12.text());
	}
}
