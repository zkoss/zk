/* B111_ZK_5641Test.java

	Purpose:

	Description:

	History:
		Mon May 18 23:49:03 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B111_ZK_5641Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		assertTrue(Boolean.parseBoolean(getEval("(function (d) {"
				+ "var r = d.getBoundingClientRect();"
				+ "return document.elementFromPoint(r.left + r.width / 2, r.top + r.height / 2) === d;"
				+ "})(jq('$dd')[0])")), "the dropdown inside the center region is painted below a body-level layer");
	}
}
