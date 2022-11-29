package org.zkoss.zephyr.webdriver.mvvm.book.basic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class ScopeparamTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		assertEquals("applicationScope-A", jq("$l11").toWidget().get("value"));
		assertEquals("sessionScope-A", jq("$l12").toWidget().get("value"));
		assertEquals("desktopScope-A", jq("$l13").toWidget().get("value"));
		assertEquals("spaceScopeScope-A", jq("$l14").toWidget().get("value"));
		assertEquals("requestScope-A", jq("$l15").toWidget().get("value"));
		assertEquals("B", jq("$l16").toWidget().get("value"));
		assertEquals("C", jq("$l17").toWidget().get("value"));
		assertEquals("E", jq("$l18").toWidget().get("value"));
		assertEquals("", jq("$l19").toWidget().get("value"));
		click(jq("$cmd1").toWidget());
		waitResponse();
		assertEquals("applicationScope-A", jq("$l11").toWidget().get("value"));
		assertEquals("sessionScope-A", jq("$l12").toWidget().get("value"));
		assertEquals("desktopScope-A", jq("$l13").toWidget().get("value"));
		assertEquals("spaceScopeScope-A", jq("$l14").toWidget().get("value"));
		assertEquals("", jq("$l15").toWidget().get("value"));
		assertEquals("F", jq("$l16").toWidget().get("value"));
		assertEquals("C", jq("$l17").toWidget().get("value"));
		assertEquals("E", jq("$l18").toWidget().get("value"));
		assertEquals("G", jq("$l19").toWidget().get("value"));
		click(jq("$cmd2").toWidget());
		waitResponse();
		assertEquals("var2 by Desktop", jq("$l11").toWidget().get("value"));
		assertEquals("var1 by Desktop", jq("$l12").toWidget().get("value"));
		assertEquals("desktopScope-A", jq("$l13").toWidget().get("value"));
		assertEquals("spaceScopeScope-A", jq("$l14").toWidget().get("value"));
		assertEquals("", jq("$l15").toWidget().get("value"));
		assertEquals("F", jq("$l16").toWidget().get("value"));
		assertEquals("C", jq("$l17").toWidget().get("value"));
		assertEquals("E", jq("$l18").toWidget().get("value"));
		assertEquals("G", jq("$l19").toWidget().get("value"));
		click(jq("$cmd3").toWidget());
		waitResponse();
		assertEquals("applicationScope-A", jq("$l11").toWidget().get("value"));
		assertEquals("sessionScope-A", jq("$l12").toWidget().get("value"));
		assertEquals("desktopScope-A", jq("$l13").toWidget().get("value"));
		assertEquals("spaceScopeScope-A", jq("$l14").toWidget().get("value"));
		assertEquals("", jq("$l15").toWidget().get("value"));
		assertEquals("F", jq("$l16").toWidget().get("value"));
		assertEquals("C", jq("$l17").toWidget().get("value"));
		assertEquals("E", jq("$l18").toWidget().get("value"));
		assertEquals("G", jq("$l19").toWidget().get("value"));
		click(jq("$cmd2").toWidget());
		waitResponse();
		assertEquals("var2 by Desktop", jq("$l11").toWidget().get("value"));
		assertEquals("var1 by Desktop", jq("$l12").toWidget().get("value"));
		assertEquals("desktopScope-A", jq("$l13").toWidget().get("value"));
		assertEquals("spaceScopeScope-A", jq("$l14").toWidget().get("value"));
		assertEquals("", jq("$l15").toWidget().get("value"));
		assertEquals("F", jq("$l16").toWidget().get("value"));
		assertEquals("C", jq("$l17").toWidget().get("value"));
		assertEquals("E", jq("$l18").toWidget().get("value"));
		assertEquals("G", jq("$l19").toWidget().get("value"));
	}
}