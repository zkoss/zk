package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B01188MixingELWithRefTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery lb = jq("$lb");
		JQuery outerItems = lb.find("@listitem.outer");
		assertEquals(2, outerItems.length());
		assertEquals("Today", outerItems.eq(0).find("@listcell.outer > @label").text());
		assertEquals("Tomorrow", outerItems.eq(1).find("@listcell.outer > @label").text());

		JQuery innerItems = outerItems.eq(0).find("@listbox > @listitem.inner");
		assertEquals(2, innerItems.length());
		assertEquals("Item 1", innerItems.eq(0).find("@listcell.inner > @label").text());
		assertEquals("Item 2", innerItems.eq(1).find("@listcell.inner > @label").text());

		innerItems = outerItems.eq(1).find("@listbox > @listitem.inner");
		assertEquals(2, innerItems.length());
		assertEquals("Item 3", innerItems.eq(0).find("@listcell.inner > @label").text());
		assertEquals("Item 4", innerItems.eq(1).find("@listcell.inner > @label").text());
	}
}
