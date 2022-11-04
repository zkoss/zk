package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B00967GroupModelTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery grid = jq("$grid");
		JQuery groups = grid.find("@group");
		JQuery groupfoots = grid.find("@groupfoot");
		JQuery rows = grid.find("@row");

		assertEquals(3, groups.length());
		assertEquals(3, groupfoots.length());
		assertEquals(5, rows.length());

		assertEquals("Fruits", groups.eq(0).toWidget().get("label"));
		assertEquals("Seafood", groups.eq(1).toWidget().get("label"));
		assertEquals("Vegetables", groups.eq(2).toWidget().get("label"));

		assertEquals("1", groupfoots.eq(0).find("@label").text());
		assertEquals("2", groupfoots.eq(1).find("@label").text());
		assertEquals("2", groupfoots.eq(2).find("@label").text());

		assertEquals("Apples", rows.eq(0).find("@label").eq(1).text());
		assertEquals("Salmon", rows.eq(1).find("@label").eq(1).text());
		assertEquals("Shrimp", rows.eq(2).find("@label").eq(1).text());
		assertEquals("Asparagus", rows.eq(3).find("@label").eq(1).text());
		assertEquals("Beets", rows.eq(4).find("@label").eq(1).text());
	}
}
