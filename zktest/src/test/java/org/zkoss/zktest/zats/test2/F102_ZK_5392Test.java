/* F102_ZK_5392Test.java

	Purpose:

	Description:

	History:
		Fri Mar 28 11:51:21 CST 2025, Created by jameschu

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.Element;
import org.zkoss.test.webdriver.ztl.JQuery;
import org.zkoss.test.webdriver.ztl.Widget;

/**
 * @author jameschu
 */
public class F102_ZK_5392Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		//test first datebox
		Widget db = jq("$db1").toWidget();
		click(db.$n("btn"));
		waitResponse();
		JQuery dbPp = jq(db.$n("pp"));
		assertEquals(true, dbPp
				.find(".z-calendar-cell:contains(15)")
				.hasClass("z-calendar-disabled"));
		assertEquals(true, dbPp
				.find(".z-calendar-cell:contains(16)")
				.hasClass("z-calendar-disabled"));
		assertEquals(true, dbPp
				.find(".z-calendar-cell:contains(17)")
				.hasClass("z-calendar-disabled"));
		click(dbPp.find(".z-calendar-cell:contains(18)"));
		waitResponse();
		Element inp = db.$n("real");
		assertEquals("Jan 18, 2023", inp.get("value"));
		type(inp, "Jan 15, 2023");
		blur(inp);
		waitResponse();
		assertTrue(hasError());
		assertEquals("The date is disabled", jq(".z-errorbox-content").text());
		type(inp, "Jan 18, 2023");
		blur(inp);
		waitResponse();
		assertNoAnyError();
		//test third datebox
		db = jq("$db3").toWidget();
		click(db.$n("btn"));
		waitResponse();
		dbPp = jq(db.$n("pp"));
		assertEquals(true, dbPp
				.find(".z-calendar-cell:contains(15)")
				.hasClass("z-calendar-disabled"));
		assertEquals(true, dbPp
				.find(".z-calendar-cell:contains(16)")
				.hasClass("z-calendar-disabled"));
		assertEquals(true, dbPp
				.find(".z-calendar-cell:contains(17)")
				.hasClass("z-calendar-disabled"));
		assertEquals(true, dbPp
				.find(".z-calendar-cell:contains(18)")
				.hasClass("z-calendar-disabled"));
		click(dbPp.find(".z-calendar-cell:contains(22)"));
		waitResponse();
		inp = db.$n("real");
		assertEquals("Jan 22, 2023", inp.get("value"));
		type(inp, "Jan 25, 2023");
		blur(inp);
		waitResponse();
		assertTrue(hasError());
		assertEquals("The date is disabled", jq(".z-errorbox-content").text());
		type(inp, "Jan 22, 2023");
		blur(inp);
		waitResponse();
		assertNoAnyError();
	}
}
