/* F104_ZK_5409_LibPropFallbackTest.java

	Purpose:

	Description:

	History:
		Tue May 12 10:22:52 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.Dimension;
import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * Verifies the fallback chain {@code getEffectiveResponsive()}:
 * when &lt;grid&gt; has no {@code responsive} attribute, a page-scope
 * attribute {@code org.zkoss.zul.grid.responsive} should still drive
 * stacking. This exercises the same chain that {@code zk.xml}
 * {@code &lt;library-property&gt;} settings use (page → desktop → library),
 * without mutating the global zk.xml.
 */
public class F104_ZK_5409_LibPropFallbackTest extends WebDriverTestCase {

	@Test
	public void testPageScopeAttributeFallback() {
		connect("/test2/F104-ZK-5409-libprop.zul");
		waitResponse();
		Dimension original = driver.manage().window().getSize();
		try {
			// Wide → table mode
			driver.manage().window().setSize(new Dimension(1200, original.height));
			waitResponse();
			JQuery grid = jq("$g1");
			assertFalse(grid.hasClass("z-grid--stacking"),
					"At 1200px, even with page fallback set, grid should be in table mode");

			// Narrow → should pick up page-scope fallback and stack
			driver.manage().window().setSize(new Dimension(400, original.height));
			waitResponse();
			assertTrue(jq("$g1").hasClass("z-grid--stacking"),
					"At 400px, <grid> with no responsive attribute should inherit "
					+ "page-scope org.zkoss.zul.grid.responsive=stacking and stack");
		} finally {
			driver.manage().window().setSize(original);
		}
	}
}
