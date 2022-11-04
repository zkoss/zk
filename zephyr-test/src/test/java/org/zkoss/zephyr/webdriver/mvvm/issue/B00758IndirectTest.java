package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B00758IndirectTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery grid = jq("$grid"); //two window element is forbidden!!
		JQuery btn1 = jq("$btn1");
		JQuery btn2 = jq("$btn2");
		JQuery rows = grid.find("@row");

		assertEquals("First0", rows.eq(0).find("@textbox").eq(0).val());
		assertEquals("Last0", rows.eq(0).find("@textbox").eq(1).val());
		assertEquals("First0 Last0", rows.eq(0).find("@label").text());

		click(btn1);
		waitResponse();
		rows = grid.find("@row");
		assertEquals("Tom", rows.eq(0).find("@textbox").eq(0).val());
		assertEquals("Last0", rows.eq(0).find("@textbox").eq(1).val());
		assertEquals("Tom Last0", rows.eq(0).find("@label").text());

		click(btn2);
		waitResponse();
		rows = grid.find("@row");
		assertEquals("Henri", rows.eq(0).find("@textbox").eq(0).val());
		assertEquals("Chen", rows.eq(0).find("@textbox").eq(1).val());
		assertEquals("Henri Chen", rows.eq(0).find("@label").text());
	}
}
