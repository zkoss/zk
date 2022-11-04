package org.zkoss.zephyr.webdriver.mvvm.issue;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B0004Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		//the following five are labels
		JQuery l11 = jq("$l11");
		JQuery l12 = jq("$l12");
		JQuery msg1 = jq("$msg1");
		JQuery msg2 = jq("$msg2");
		JQuery msg3 = jq("$msg3");
		Assertions.assertEquals("0", l11.text());
		Assertions.assertEquals("", l12.text());
		Assertions.assertEquals("", msg1.text());
		Assertions.assertEquals("", msg2.text());
		Assertions.assertEquals("", msg3.text());

		JQuery btn1 = jq("$btn1");
		click(btn1);
		waitResponse();
		Assertions.assertEquals("0", l11.text());
		Assertions.assertEquals("", l12.text());
		Assertions.assertEquals("value 1 have to large than 10", msg1.text());
		Assertions.assertEquals("", msg2.text());
		Assertions.assertEquals("", msg3.text());

		JQuery t21 = jq("$t21");
		type(t21, "32");
		waitResponse();
		Assertions.assertEquals("0", l11.text());
		Assertions.assertEquals("", l12.text());
		Assertions.assertEquals("", msg1.text());
		Assertions.assertEquals("", msg2.text());
		Assertions.assertEquals("", msg3.text());

		click(btn1);
		waitResponse();
		Assertions.assertEquals("0", l11.text());
		Assertions.assertEquals("", l12.text());
		Assertions.assertEquals("", msg1.text());
		Assertions.assertEquals("value 2 is not valid For input string: \"\"", msg2.text());
		Assertions.assertEquals("", msg3.text());

		JQuery t22 = jq("$t22");
		type(t22, "13");
		waitResponse();
		Assertions.assertEquals("0", l11.text());
		Assertions.assertEquals("", l12.text());
		Assertions.assertEquals("", msg1.text());
		Assertions.assertEquals("value 2 have to large than 20", msg2.text());
		Assertions.assertEquals("", msg3.text());

		type(t22, "22");
		waitResponse();
		Assertions.assertEquals("0", l11.text());
		Assertions.assertEquals("", l12.text());
		Assertions.assertEquals("", msg1.text());
		Assertions.assertEquals("", msg2.text());
		Assertions.assertEquals("", msg3.text());

		click(btn1);
		waitResponse();
		Assertions.assertEquals("0", l11.text());
		Assertions.assertEquals("", l12.text());
		Assertions.assertEquals("", msg1.text());
		Assertions.assertEquals("value 2 have to large than value 1", msg2.text());
		Assertions.assertEquals("", msg3.text());

		type(t22, "42");
		waitResponse();
		Assertions.assertEquals("0", l11.text());
		Assertions.assertEquals("", l12.text());
		Assertions.assertEquals("", msg1.text());
		Assertions.assertEquals("", msg2.text());
		Assertions.assertEquals("", msg3.text());

		click(btn1);
		waitResponse();
		Assertions.assertEquals("32", l11.text());
		Assertions.assertEquals("42", l12.text());
		Assertions.assertEquals("", msg1.text());
		Assertions.assertEquals("", msg2.text());
		Assertions.assertEquals("execute command 1", msg3.text());
	}
}
