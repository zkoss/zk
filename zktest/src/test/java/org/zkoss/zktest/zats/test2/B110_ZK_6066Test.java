/* B110_ZK_6066Test.java

	Purpose:

	Description:

	History:
		Tue Sep 08 17:55:23 CST 2026, Created by peggypeng

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

@Tag("WcagTestOnly")
public class B110_ZK_6066Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();
		waitResponse();

		JQuery mold = jq(".z-checkbox-switch").find(".z-checkbox-mold");
		assertEquals("switch", mold.attr("role"));
		assertEquals("false", mold.attr("aria-checked"));
		assertEquals("false", mold.attr("aria-disabled"));
	}
}
