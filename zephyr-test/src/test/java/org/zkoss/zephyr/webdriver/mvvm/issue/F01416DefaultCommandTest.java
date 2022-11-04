package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class F01416DefaultCommandTest extends WebDriverTestCase {

	@Test
	public void test() {
		connect();

		JQuery btn1 = jq("$myWin1 $btn1");
		JQuery btn2 = jq("$myWin1 $btn2");
		JQuery btng1 = jq("$myWin1 $btng1");
		JQuery btng2 = jq("$myWin1 $btng2");
		JQuery lb1 = jq("$myWin1 $lb1");
		JQuery btn3 = jq("$myWin2 $btn3");
		JQuery btn4 = jq("$myWin2 $btn4");
		JQuery btng3 = jq("$myWin2 $btng3");
		JQuery btng4 = jq("$myWin2 $btng4");
		JQuery lb2 = jq("$myWin2 $lb2");

		assertEquals("Dennis", lb1.text());
		assertEquals("Dennis", lb2.text());

		click(btn1);
		waitResponse();
		assertEquals("do command1", lb1.text());
		assertEquals("Dennis", lb2.text());

		click(btn2);
		waitResponse();
		assertEquals("do command cmd2", lb1.text());
		assertEquals("Dennis", lb2.text());

		click(btng1);
		waitResponse();
		assertEquals("do globa-command1", lb1.text());
		assertEquals("do globa-command1", lb2.text());

		click(btng2);
		waitResponse();
		assertEquals("do globa-command gcmd2", lb1.text());
		assertEquals("do globa-command gcmd2", lb2.text());

		click(btn3);
		waitResponse();
		assertEquals("do globa-command gcmd2", lb1.text());
		assertEquals("do command3", lb2.text());

		click(btn4);
		waitResponse();
		assertEquals("do globa-command gcmd2", lb1.text());
		assertEquals("do command cmd4", lb2.text());

		click(btng3);
		waitResponse();
		assertEquals("do globa-command3", lb1.text());
		assertEquals("do globa-command3", lb2.text());

		click(btng4);
		waitResponse();
		assertEquals("do globa-command gcmd4", lb1.text());
		assertEquals("do globa-command gcmd4", lb2.text());
	}
}
