package org.zkoss.zktest.zats.bind.basic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class Load_save_formTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		JQuery t21 = jq("$t21");
		t21.toElement().set("value", "");
		sendKeys(t21.toWidget(), "X");
		waitResponse();
		assertEquals("A", jq("$l11").toWidget().get("value"));
		assertEquals("B", jq("$l12").toWidget().get("value"));
		assertEquals("C", jq("$l13").toWidget().get("value"));
		assertEquals("A", jq("$l14").toWidget().get("value"));
		assertEquals("B", jq("$l15").toWidget().get("value"));
		assertEquals("C", jq("$l16").toWidget().get("value"));
		click(jq("$btn1").toWidget());
		waitResponse();
		assertEquals("A", jq("$l11").toWidget().get("value"));
		assertEquals("X", jq("$l12").toWidget().get("value"));
		assertEquals("C", jq("$l13").toWidget().get("value"));
		assertEquals("A", jq("$l14").toWidget().get("value"));
		assertEquals("X", jq("$l15").toWidget().get("value"));
		assertEquals("X", jq("$l16").toWidget().get("value"));
		t21.toElement().set("value", "");
		sendKeys(t21.toWidget(), "Y");
		waitResponse();
		assertEquals("A", jq("$l11").toWidget().get("value"));
		assertEquals("X", jq("$l12").toWidget().get("value"));
		assertEquals("C", jq("$l13").toWidget().get("value"));
		assertEquals("A", jq("$l14").toWidget().get("value"));
		assertEquals("X", jq("$l15").toWidget().get("value"));
		assertEquals("X", jq("$l16").toWidget().get("value"));
		click(jq("$btn2").toWidget());
		waitResponse();
		assertEquals("A", jq("$l11").toWidget().get("value"));
		assertEquals("X", jq("$l12").toWidget().get("value"));
		assertEquals("Y", jq("$l13").toWidget().get("value"));
		assertEquals("A", jq("$l14").toWidget().get("value"));
		assertEquals("X", jq("$l15").toWidget().get("value"));
		assertEquals("Y", jq("$l16").toWidget().get("value"));

		t21.toElement().set("value", "");
		sendKeys(t21.toWidget(), "Z");
		waitResponse();
		assertEquals("A", jq("$l11").toWidget().get("value"));
		assertEquals("X", jq("$l12").toWidget().get("value"));
		assertEquals("Y", jq("$l13").toWidget().get("value"));
		assertEquals("A", jq("$l14").toWidget().get("value"));
		assertEquals("X", jq("$l15").toWidget().get("value"));
		assertEquals("Y", jq("$l16").toWidget().get("value"));
		click(jq("$btn3"));
		waitResponse();
		assertEquals("A", jq("$l11").toWidget().get("value"));
		assertEquals("X", jq("$l12").toWidget().get("value"));
		assertEquals("Z", jq("$l13").toWidget().get("value"));
		assertEquals("A", jq("$l14").toWidget().get("value"));
		assertEquals("X", jq("$l15").toWidget().get("value"));
		assertEquals("Z", jq("$l16").toWidget().get("value"));
	}
}
