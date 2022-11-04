package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B01059DifferentTypeTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		JQuery ib1 = jq("$ib1");
		JQuery lb2 = jq("$lb2");
		JQuery ib2 = jq("$ib2");
		JQuery ib3 = jq("$ib3");
		type(ib1, "1");
		waitResponse();
		assertTrue(jq(".z-messagebox @label").text().contains("Property 'value1' not writable"));
		click(jq(".z-messagebox-buttons").find("button"));
		waitResponse();
		type(ib2, "2");
		waitResponse();
		assertEquals("2", lb2.text());
		type(ib3, "3");
		waitResponse();
		assertTrue(jq(".z-messagebox @label").text().contains("Property 'value3' not writable"));
	}
}
