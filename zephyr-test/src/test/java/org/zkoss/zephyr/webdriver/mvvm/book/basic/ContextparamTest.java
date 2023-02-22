package org.zkoss.zephyr.webdriver.mvvm.book.basic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class ContextparamTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect("/mvvm/book/basic/contextparam.zul");
		assertEquals("applicationScope-A", jq("$l11").toWidget().get("value"));
		assertEquals("sessionScope-A", jq("$l12").toWidget().get("value"));
		assertEquals("desktopScope-A", jq("$l13").toWidget().get("value"));
		assertEquals("spaceScope-A", jq("$l14").toWidget().get("value"));
		assertEquals("requestScope-A", jq("$l15").toWidget().get("value"));
		assertEquals("componentScope-B", jq("$l16").toWidget().get("value"));
		assertEquals("vbox1", jq("$l17").toWidget().get("value"));
		assertEquals("vbox1", jq("$l18").toWidget().get("value"));
		assertEquals("true", jq("$l19").toWidget().get("value"));
		assertEquals("true", jq("$l1a").toWidget().get("value"));
		click(jq("$cmd1").toWidget());
		waitResponse();
		assertEquals("applicationScope-A", jq("$l11").toWidget().get("value"));
		assertEquals("sessionScope-A", jq("$l12").toWidget().get("value"));
		assertEquals("desktopScope-A", jq("$l13").toWidget().get("value"));
		assertEquals("spaceScope-A", jq("$l14").toWidget().get("value"));
		assertEquals("", jq("$l15").toWidget().get("value"));
		assertEquals("componentScope-C", jq("$l16").toWidget().get("value"));
		assertEquals("cmd1", jq("$l17").toWidget().get("value"));
		assertEquals("vbox1", jq("$l18").toWidget().get("value"));
		assertEquals("false", jq("$l19").toWidget().get("value"));
		assertEquals("false", jq("$l1a").toWidget().get("value"));
		click(jq("$cmd2").toWidget());
		waitResponse();
		assertEquals("var2 by Desktop", jq("$l11").toWidget().get("value"));
		assertEquals("var1 by Desktop", jq("$l12").toWidget().get("value"));
		assertEquals("desktopScope-A", jq("$l13").toWidget().get("value"));
		assertEquals("spaceScope-Y", jq("$l14").toWidget().get("value"));
		assertEquals("", jq("$l15").toWidget().get("value"));
		assertEquals("componentScope-C", jq("$l16").toWidget().get("value"));
		assertEquals("cmd2", jq("$l17").toWidget().get("value"));
		assertEquals("vbox1", jq("$l18").toWidget().get("value"));
		assertEquals("false", jq("$l19").toWidget().get("value"));
		assertEquals("false", jq("$l1a").toWidget().get("value"));
		click(jq("$cmd3").toWidget());
		waitResponse();
		assertEquals("applicationScope-A", jq("$l11").toWidget().get("value"));
		assertEquals("sessionScope-A", jq("$l12").toWidget().get("value"));
		assertEquals("desktopScope-A", jq("$l13").toWidget().get("value"));
		assertEquals("spaceScope-Y", jq("$l14").toWidget().get("value"));
		assertEquals("", jq("$l15").toWidget().get("value"));
		assertEquals("componentScope-C", jq("$l16").toWidget().get("value"));
		assertEquals("cmd3", jq("$l17").toWidget().get("value"));
		assertEquals("vbox1", jq("$l18").toWidget().get("value"));
		assertEquals("false", jq("$l19").toWidget().get("value"));
		assertEquals("false", jq("$l1a").toWidget().get("value"));
	}
}
