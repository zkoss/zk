package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B00619Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();
		waitResponse();

		JQuery listbox = jq("$listbox");
		assertEquals("1", listbox.toWidget().get("selectedIndex"));

		JQuery tabbox = jq("$tabbox");
		assertEquals("1", tabbox.toWidget().get("selectedIndex"));

		assertFalse(jq("$taba").toWidget().is("selected"));
		assertTrue(jq("$tabb").toWidget().is("selected"));
		assertFalse(jq("$tabc").toWidget().is("selected"));

		JQuery itema = jq("$itema");
		click(itema);
		waitResponse();
		assertEquals("0", listbox.toWidget().get("selectedIndex"));
		assertEquals("0", tabbox.toWidget().get("selectedIndex"));
		assertTrue(jq("$taba").toWidget().is("selected"));
		assertFalse(jq("$tabb").toWidget().is("selected"));
		assertFalse(jq("$tabc").toWidget().is("selected"));

		click(jq("$tabc"));
		waitResponse();
		assertEquals("2", listbox.toWidget().get("selectedIndex"));
		assertEquals("2", tabbox.toWidget().get("selectedIndex"));
		assertFalse(jq("$taba").toWidget().is("selected"));
		assertFalse(jq("$tabb").toWidget().is("selected"));
		assertTrue(jq("$tabc").toWidget().is("selected"));
	}
}
