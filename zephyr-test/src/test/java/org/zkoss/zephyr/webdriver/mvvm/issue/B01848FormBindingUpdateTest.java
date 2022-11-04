package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B01848FormBindingUpdateTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery combobox = jq("$combobox");
		JQuery lb1 = jq("$lb1");
		JQuery lb2 = jq("$lb2");

		type(combobox.find("input"), "Item 1");
		waitResponse();
		assertTrue(lb1.text().contains("org.zkoss.zktest.bind.issue.B01848FormBindingUpdate"));
		assertEquals("Item 1", lb2.text());

	}
}
