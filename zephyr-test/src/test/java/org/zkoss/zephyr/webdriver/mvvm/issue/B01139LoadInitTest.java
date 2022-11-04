package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B01139LoadInitTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery liChk = jq("$liChk");
		JQuery changeNameBtn = jq("$changeNameBtn");
		JQuery nameTexb = jq("$nameTexb");
		JQuery nameLbl = jq("$nameLbl");

		type(nameTexb, "XYZ");
		waitResponse();
		click(changeNameBtn);
		waitResponse();
		assertEquals("XYZ", nameLbl.text());

		type(nameTexb, "XXX");
		waitResponse();
		click(liChk);
		waitResponse();
		click(changeNameBtn);
		waitResponse();
		assertEquals("XYZ", nameLbl.text());

		type(nameTexb, "XXX");
		waitResponse();
		click(liChk);
		waitResponse();
		click(changeNameBtn);
		waitResponse();
		assertEquals("XXX", nameLbl.text());
	}
}
