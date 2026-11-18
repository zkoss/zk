/* B110_ZK_6112_MarbleDensityApplyTest.java

	Purpose:

	Description:

	History:
		Tue Nov 17 2026, Created for ZK-6112.

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;
import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * MarbleDensity.apply(Component, Density) puts data-density into the DOM on the first call for
 * an already rendered component, and again when switching back (ZK-6112).
 */
public class B110_ZK_6112_MarbleDensityApplyTest extends WebDriverTestCase {
	@Test
	public void testFirstApplyReachesTheDom() {
		connect();
		waitResponse();
		assertFalse(hasDensityAttribute());

		click(jq("$compact"));
		waitResponse();
		assertEquals("compact", densityOfRegion());

		click(jq("$comfortable"));
		waitResponse();
		assertEquals("comfortable", densityOfRegion());
	}

	private boolean hasDensityAttribute() {
		return Boolean.parseBoolean(getEval("zk.Widget.$('$region').$n().hasAttribute('data-density')"));
	}

	private String densityOfRegion() {
		return jq("$region").attr("data-density");
	}
}
