package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B00992SubModelTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery combobox = jq("$combobox");
		JQuery lab = jq("$lab");

		type(combobox.find("input"), "9");
		waitResponse();        //System.out.println("*********" + combobox.getValue());
		assertEquals("9", lab.text());

		//can't OpenAgent.open()!!!!!! why!!!!
		type(combobox.find("input"), "9");
		waitResponse();
		JQuery w = combobox.find("@comboitem").eq(10);
		assertEquals("99", w.toWidget().get("label"));


		click(w);
		waitResponse();
		assertEquals("99", lab.text());

	}
}
