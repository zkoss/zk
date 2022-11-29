package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B00714Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery outerbox = jq("$outerbox");
		JQuery lb = jq("$lb");
		JQuery reload = jq("$reload");
		JQuery invalidate = jq("$invalidate");
		JQuery items = outerbox.find("@listitem");

		assertEquals(2, items.length());
		assertEquals("A0", items.eq(0).find(".z-listcell-content").eq(0).text());
		assertEquals("A0 0", items.eq(1).find(".z-listcell-content").eq(0).text());
		assertEquals("", lb.text());

		click(items.eq(0));
		waitResponse();
		assertEquals("A0", lb.text());

		click(reload);
		waitResponse();
		items = outerbox.find("@listitem");
		assertEquals("A0", items.eq(0).find(".z-listcell-content").eq(0).text());
		assertEquals("A0 0", items.eq(1).find(".z-listcell-content").eq(0).text());
		assertEquals("A0", lb.text());

		click(invalidate);
		waitResponse();
		items = outerbox.find("@listitem");
		assertEquals("A0", items.eq(0).find(".z-listcell-content").eq(0).text());
		assertEquals("A0 0", items.eq(1).find(".z-listcell-content").eq(0).text());
		assertEquals("A0", lb.text());
		assertNoAnyError();
	}
}
