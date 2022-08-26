package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B50_3132161Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		JQuery db = jq("$db");
		String pattern = "\\w{3} \\w{3} \\d{2} \\d{2}:00:00 CST \\d{4}";
		click(db.toWidget().$n("btn"));
		waitResponse();
		click(jq(".z-calendar-selected"));
		waitResponse();
		assertTrue(jq("@label:eq(1)").text().matches(pattern));
    }
}
