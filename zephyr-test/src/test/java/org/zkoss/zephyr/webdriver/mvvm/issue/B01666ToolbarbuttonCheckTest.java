package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B01666ToolbarbuttonCheckTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery checkedLab = jq("$checkedLab");
		JQuery messageLab = jq("$messageLab");
		JQuery btn1 = jq("$btn1");

		click(btn1);
		waitResponse();
		assertEquals("false", checkedLab.text());
		assertEquals("checked false", messageLab.text());

		click(btn1);
		waitResponse();
		assertEquals("true", checkedLab.text());
		assertEquals("checked true", messageLab.text());
	}
}
