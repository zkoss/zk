/* F110_ZK_6065Test.java

        Purpose:
                
        Description:
                
        History:
                Thu Aug 06 15:00:36 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.zkoss.test.webdriver.WebDriverTestCase;

public class F110_ZK_6065Test extends WebDriverTestCase {
	private static final int SCROLL_LEFT = 37;

	@Test
	public void test() {
		connect();
		waitResponse();
		assertFrozenSmooth("$grid", "grid", "Grid");
		assertFrozenSmooth("$listbox", "listbox", "Listbox");
	}

	private void assertFrozenSmooth(String idSelector, String componentName, String name) {
		assertTrue(count(idSelector, "z-frozen-col") > 0, name + " should freeze left columns");
		assertTrue(count(idSelector, "z-frozen-right-col") > 0, name + " should freeze right columns");

		jq(idSelector + " .z-frozen-inner").scrollLeft(SCROLL_LEFT);
		waitResponse();

		assertEquals(SCROLL_LEFT, jq(idSelector + " .z-frozen-inner").scrollLeft(),
				name + " frozen scrollbar should preserve the pixel offset");
		assertEquals(SCROLL_LEFT, jq(idSelector + " .z-" + componentName + "-header").scrollLeft(),
				name + " header should scroll smoothly");
		assertEquals(SCROLL_LEFT, jq(idSelector + " .z-" + componentName + "-body").scrollLeft(),
				name + " body should scroll smoothly");
	}

	private int count(String idSelector, String cssClass) {
		return Integer.parseInt(getEval(
				"jq('" + idSelector + "').find('." + cssClass + "').length"));
	}
}
