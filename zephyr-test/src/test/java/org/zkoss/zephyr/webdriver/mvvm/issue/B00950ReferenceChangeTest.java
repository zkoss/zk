package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B00950ReferenceChangeTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery listbox = jq("$listbox");
		JQuery l11 = jq("$l11");
		JQuery l12 = jq("$l12");
		JQuery l13 = jq("$l13");
		JQuery l21 = jq("$l21");
		JQuery l22 = jq("$l22");
		JQuery l23 = jq("$l23");
		JQuery clear = jq("$clear");

		assertEquals("", l11.text());
		assertEquals("", l12.text());
		assertEquals("", l13.text());
		assertEquals("", l21.text());
		assertEquals("", l22.text());
		assertEquals("", l23.text());

		click(listbox.find("@listitem"));
		waitResponse();

		assertEquals("Dennis", l11.text());
		assertEquals("Chen", l12.text());
		assertEquals("Dennis Chen", l13.text());
		assertEquals("Dennis", l21.text());
		assertEquals("Chen", l22.text());
		assertEquals("Dennis Chen", l23.text());

		click(listbox.find("@listitem").eq(1));
		waitResponse();
		assertEquals("Alice", l11.text());
		assertEquals("Lin", l12.text());
		assertEquals("Alice Lin", l13.text());
		assertEquals("Alice", l21.text());
		assertEquals("Lin", l22.text());
		assertEquals("Alice Lin", l23.text());

		click(listbox.find("@listitem").eq(2));
		waitResponse();
		assertEquals("", l11.text());
		assertEquals("", l12.text());
		assertEquals("", l13.text());
		assertEquals("", l21.text());
		assertEquals("", l22.text());
		assertEquals("", l23.text());

		click(listbox.find("@listitem").eq(1));
		waitResponse();
		type(listbox.find("@listitem").eq(1).find("@textbox"), "Grace");
		waitResponse();
		assertEquals("Grace", l11.text());
		assertEquals("Lin", l12.text());
		assertEquals("Grace Lin", l13.text());
		assertEquals("Grace", l21.text());
		assertEquals("Lin", l22.text());
		assertEquals("Grace Lin", l23.text());

		click(clear);
		waitResponse();
		assertEquals("", l11.text());
		assertEquals("", l12.text());
		assertEquals("", l13.text());
		assertEquals("", l21.text());
		assertEquals("", l22.text());
		assertEquals("", l23.text());
	}
}
