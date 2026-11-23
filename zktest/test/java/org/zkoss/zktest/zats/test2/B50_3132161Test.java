package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

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
