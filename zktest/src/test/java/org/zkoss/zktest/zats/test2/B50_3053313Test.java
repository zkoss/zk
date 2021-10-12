package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;
import org.zkoss.zktest.zats.ztl.Widget;

/**
 * @author jameschu
 */
public class B50_3053313Test extends WebDriverTestCase {
	@Test public void test() {
		connect();
		Widget dtbx1 = jq("$dtbx1").toWidget();
		Widget dtbx2 = jq("$dtbx2").toWidget();
		Widget dtbx3 = jq("$dtbx3").toWidget();

		int dt1 = 0;
		click(dtbx3.$n("btn"));
		waitResponse();
		JQuery calRows = jq(dtbx3.$n("pp")).find("tbody tr");
		int today = Integer.parseInt(jq(".z-calendar-selected").last().text());

		List<JQuery> l = new ArrayList<>();
		for (int i = 0;i < calRows.length(); i++) {
			l.add(calRows.eq(i).find("td"));
			String a = "\"";
		}
		boolean foundToday = false;
		for (int j = 0; j < l.size(); j++) {
			for (int k = 0; k < l.get(j).length(); k++) {
				JQuery td = l.get(j).eq(k);
				String clsnm = td.attr("class");
				if (td.isVisible()) {
					dt1 = parseInt(td.html());
					if (!foundToday) {
						assertFalse(clsnm.contains("z-calendar-disabled"));
						foundToday = (dt1 == today) && (!clsnm.contains("z-calendar-outside"));
					} else {
						assertTrue(clsnm.contains("z-calendar-disabled"));
					}
				}
			}
		}

		click(dtbx2.$n("btn"));
		waitResponse();
		dt1 = parseInt(jq(dtbx2.$n("pp"))
				.find(".z-calendar-disabled").get(0).get("innerHTML"));
		assertTrue("for second datebox, the only unselectable date should be today",
				(dt1 == today) && (jq(dtbx2.$n("pp")).find(".z-calendar-disabled").length() == 1));

		click(dtbx1.$n("btn"));
		waitResponse();
		dt1 = parseInt(jq(dtbx1.$n("pp"))
				.find(".z-calendar-disabled").last().get(0).get("innerHTML"));
		assertTrue("for first datebox, the last unselectable date should be yesterday",
				(today - dt1 == 1) || ((today == 1) && (31 - dt1 <= 3)));
	}
}