/* B110_ZK_6169Test.java

	Purpose:

	Description:

	History:
		Thu Oct 08 18:02:39 CST 2026, Created by peggypeng

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

@Tag("WcagTestOnly")
public class B110_ZK_6169Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery gridEmptybody = jq("$grid").find(".z-grid-emptybody");
		JQuery emptyGridEmptybody = jq("$emptyGrid").find(".z-grid-emptybody");
		JQuery listboxEmptybody = jq("$listbox").find(".z-listbox-emptybody");
		checkEmptyBody(gridEmptybody, false);
		checkEmptyBody(emptyGridEmptybody, false);
		checkEmptyBody(listboxEmptybody, false);
		checkEmptyBody(jq("$loadedGrid").find(".z-grid-emptybody"), true);
		checkEmptyBody(jq("$loadedListbox").find(".z-listbox-emptybody"), true);

		click(jq("$add"));
		waitResponse();
		checkEmptyBody(gridEmptybody, true);
		checkEmptyBody(emptyGridEmptybody, false);
		checkEmptyBody(listboxEmptybody, true);

		click(jq("$remove"));
		waitResponse();
		checkEmptyBody(gridEmptybody, false);
		checkEmptyBody(emptyGridEmptybody, false);
		checkEmptyBody(listboxEmptybody, false);

		click(jq("$clear"));
		waitResponse();
		checkEmptyBody(gridEmptybody, true);
		checkEmptyBody(listboxEmptybody, true);
	}

	private void checkEmptyBody(JQuery emptybody, boolean hidden) {
		Assertions.assertEquals("rowgroup", emptybody.attr("role"));
		Assertions.assertEquals("row", emptybody.find("tr").attr("role"));
		Assertions.assertEquals("gridcell", emptybody.find("td").attr("role"));
		Assertions.assertEquals(hidden, emptybody.is("[aria-hidden=\"true\"]"),
				"the empty body should be exposed to assistive technology only while it shows the empty message");
	}
}
