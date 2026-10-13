/* B110_ZK_6185_TabletTest.java

        Purpose:

        Description:

        History:
                Thu Oct 15 15:30:00 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.ztl.Widget;
import org.zkoss.zktest.zats.TabletWebDriverTestCase;

public class B110_ZK_6185_TabletTest extends TabletWebDriverTestCase {

	@Test
	public void testTapClearsIndeterminate() {
		connect("/test2/B110-ZK-6185.zul");
		waitResponse();
		// checkbox-touch replaces setChecked in the default mold, so the click must still clear indeterminate
		assertEquals("true", getEval("'' + zk.tabletUIEnabled"), "tablet UI must be on");
		Widget cb = widget("$cb");
		assertTrue(cb.is("indeterminate"));

		click(cb.$n("real"));
		waitResponse();
		assertTrue(jq(cb.$n("real")).hasClass("z-icon-check"), "the checkbox-touch setChecked must have run");
		assertFalse(cb.is("indeterminate"), "widget isIndeterminate()");
		assertTrue(jq(cb).hasClass("z-checkbox-on"), "class z-checkbox-on");
		assertFalse(jq(cb).hasClass("z-checkbox-indeterminate"), "class z-checkbox-indeterminate");
	}

	@Test
	public void testClientSetCheckedClearsIndeterminate() {
		connect("/test2/B110-ZK-6185.zul");
		waitResponse();
		assertEquals("true", getEval("'' + zk.tabletUIEnabled"), "tablet UI must be on");
		Widget cb = widget("$cb");
		assertTrue(cb.is("indeterminate"));

		eval("zk.Widget.$('$cb').setChecked(true)");
		assertTrue(jq(cb.$n("real")).hasClass("z-icon-check"), "the checkbox-touch setChecked must have run");
		assertFalse(cb.is("indeterminate"), "widget isIndeterminate()");
		assertFalse(cb.$n("real").is("indeterminate"), "native indeterminate");
		assertTrue(jq(cb).hasClass("z-checkbox-on"), "class z-checkbox-on");
		assertFalse(jq(cb).hasClass("z-checkbox-indeterminate"), "class z-checkbox-indeterminate");
		if (Boolean.parseBoolean(getEval("!!window.za11y")))
			assertEquals("true", jq(cb.$n("real")).attr("aria-checked"), "aria-checked");
	}
}
