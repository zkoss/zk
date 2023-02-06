package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B00762Combobox1Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery outerbox = jq("$outerbox");
		JQuery selected = jq("$selected");
		//Converted ListModel in client MVVM is not available, ignore this
//		JQuery min = jq("$min");
		JQuery clean = jq("$clean");
		JQuery select = jq("$select");
		JQuery reload = jq("$reload");
//		JQuery showselect = jq("$showselect");

		type(outerbox.find("input"), "A");
		waitResponse();
		assertEquals("A", selected.text()); //can't sync label with selected item
//		click(showselect);
//		waitResponse();
//		assertEquals("0", min.text());

		type(outerbox.find("input"), "C");
		waitResponse();
		assertEquals("C", selected.text()); //can't sync label with selected item
//		click(showselect);
//		waitResponse();
//		assertEquals("2", min.text());

		click(clean);
		waitResponse();
		assertEquals("", outerbox.find(".z-combobox-input").val());
		assertEquals("", selected.text());
//		click(showselect);
//		waitResponse();
//		assertEquals("-1", min.text());

		click(select);
		waitResponse();
		assertEquals("B", outerbox.find(".z-combobox-input").val());
		assertEquals("B", selected.text());
//		click(showselect);
//		waitResponse();
//		assertEquals("1", min.text());
	}
}
