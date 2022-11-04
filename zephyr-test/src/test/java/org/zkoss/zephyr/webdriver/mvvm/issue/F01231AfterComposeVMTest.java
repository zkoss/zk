package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class F01231AfterComposeVMTest extends WebDriverTestCase {

	@Test
	public void test() {
		connect();

		JQuery myWin = jq("$myWin");
		JQuery headerLb = jq("$headerLb");
		JQuery nameLb = jq("$nameLb");
		JQuery descTxb = jq("$descTxb");

		assertEquals("AAAA", myWin.toWidget().get("title"));
		assertEquals("This is a label", headerLb.text());
		assertEquals("admin", nameLb.val());
		assertEquals("this is desc", descTxb.toWidget().get("value"));
	}
}
