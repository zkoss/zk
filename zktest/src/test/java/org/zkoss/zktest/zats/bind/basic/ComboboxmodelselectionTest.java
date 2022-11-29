package org.zkoss.zktest.zats.bind.basic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class ComboboxmodelselectionTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		JQuery combobox1 = jq("$cb1");
		JQuery combobox2 = jq("$cb2");
		assertEquals("", jq("$msg").toWidget().get("value"));
		eval(combobox1.toWidget() + ".open()"); // has to wait for open
		waitResponse();
		JQuery items = combobox1.find("@comboitem");
		eval(combobox1.toWidget() + ".open()"); // has to wait for open
		waitResponse();
		click(items.eq(1).toWidget());
		waitResponse();
		assertEquals("B", combobox1.toWidget().get("value"));
		assertEquals("A", combobox2.toWidget().get("value"));
		eval(combobox1.toWidget() + ".open()"); // has to wait for open
		waitResponse();
		click(items.eq(2).toWidget());
		waitResponse();
		assertEquals("C", combobox1.toWidget().get("value"));
		assertEquals("A", combobox2.toWidget().get("value"));
		click(jq("$btn1").toWidget());
		waitResponse();
		assertEquals("reloaded", jq("$msg").toWidget().get("value"));
		assertEquals("C", combobox1.toWidget().get("value"));
		assertEquals("A", combobox2.toWidget().get("value"));
		click(jq("$btn2").toWidget());
		waitResponse();
		assertEquals("selected", jq("$msg").toWidget().get("value"));
		assertEquals("C", combobox1.toWidget().get("value"));
		assertEquals("D", combobox2.toWidget().get("value"));
	}
}
