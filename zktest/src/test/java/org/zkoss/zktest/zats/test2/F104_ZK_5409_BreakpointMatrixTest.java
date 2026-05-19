/* F104_ZK_5409_BreakpointMatrixTest.java

        Purpose:
                
        Description:
                
        History:
                Sat Apr 25 22:37:14 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/


package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.Dimension;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class F104_ZK_5409_BreakpointMatrixTest extends WebDriverTestCase {

	private static final String PATH = "/test2/F104-ZK-5409-responsive-grid-breakpoint-all.zul";

	private void resize(int width) {
		Dimension cur = driver.manage().window().getSize();
		driver.manage().window().setSize(new Dimension(width, cur.height));
		waitResponse();
	}

	private void assertStacking(String gridId, boolean expectStacking, int width) {
		boolean actual = jq("$" + gridId).hasClass("z-grid--stacking");
		assertEquals(expectStacking, actual,
				"At window width=" + width + "px, " + gridId + " expected stacking="
						+ expectStacking + " but was " + actual);
	}

	/** At 380px window (~365px container), the md/lg/xl/xxl grids stack.
	 *
	 *  <p>gridSm uses {@code responsiveColumns="sm-none"}; under the cascade
	 *  rules every tier (including xs at 380 px) resolves to {@code none},
	 *  so it stays in table mode at all widths. */
	@Test
	public void testAllStackingAt380() {
		connect(PATH);
		waitResponse();
		resize(380);

		assertStacking("gridSm",  false, 380); // sm-none cascades to xs → never stacks
		assertStacking("gridMd",  true, 380);
		assertStacking("gridLg",  true, 380);
		assertStacking("gridXl",  true, 380);
		assertStacking("gridXxl", true, 380);
	}

	/** 700px window (~685px container) crosses the sm boundary (576) — sm
	 *  grid returns to table; md/lg/xl/xxl still stack. */
	@Test
	public void testSmRestoresAt700() {
		connect(PATH);
		waitResponse();
		resize(700);

		assertStacking("gridSm",  false, 700);
		assertStacking("gridMd",  true,  700);
		assertStacking("gridLg",  true,  700);
		assertStacking("gridXl",  true,  700);
		assertStacking("gridXxl", true,  700);
	}

	/** 900px window (~885px container) crosses md (768) — sm and md restore;
	 *  lg/xl/xxl still stack. Tablet-landscape scenario. */
	@Test
	public void testMdRestoresAt900() {
		connect(PATH);
		waitResponse();
		resize(900);

		assertStacking("gridSm",  false, 900);
		assertStacking("gridMd",  false, 900);
		assertStacking("gridLg",  true,  900);
		assertStacking("gridXl",  true,  900);
		assertStacking("gridXxl", true,  900);
	}

	/** 1100px window (~1085px container) crosses lg (992) — sm/md/lg restore;
	 *  xl/xxl still stack. Small laptop. */
	@Test
	public void testLgRestoresAt1100() {
		connect(PATH);
		waitResponse();
		resize(1100);

		assertStacking("gridSm",  false, 1100);
		assertStacking("gridMd",  false, 1100);
		assertStacking("gridLg",  false, 1100);
		assertStacking("gridXl",  true,  1100);
		assertStacking("gridXxl", true,  1100);
	}

	/** 1300px window (~1285px container) crosses xl (1200) — only xxl stacks. */
	@Test
	public void testXlRestoresAt1300() {
		connect(PATH);
		waitResponse();
		resize(1300);

		assertStacking("gridSm",  false, 1300);
		assertStacking("gridMd",  false, 1300);
		assertStacking("gridLg",  false, 1300);
		assertStacking("gridXl",  false, 1300);
		assertStacking("gridXxl", true,  1300);
	}

	/** 1500px window (~1485px container) — every tier's threshold is below
	 *  1485 → ALL grids in table mode. Wide-desktop scenario. */
	@Test
	public void testNoneStackingAt1500() {
		connect(PATH);
		waitResponse();
		resize(1500);

		assertStacking("gridSm",  false, 1500);
		assertStacking("gridMd",  false, 1500);
		assertStacking("gridLg",  false, 1500);
		assertStacking("gridXl",  false, 1500);
		assertStacking("gridXxl", false, 1500);
	}

	/** Round-trip: shrink down to 380 then grow back to 1500 in a single
	 *  test — verifies the ResizeObserver tracks both directions cleanly.
	 *
	 *  <p>gridSm cannot be used here because under the cascade rules
	 *  {@code responsiveColumns="sm-none"} resolves to table at every
	 *  width. We use gridXxl + gridLg, both of which round-trip cleanly. */
	@Test
	public void testRoundTrip() {
		connect(PATH);
		waitResponse();

		resize(1500);
		assertStacking("gridXxl", false, 1500);
		assertStacking("gridLg",  false, 1500);

		resize(380);
		assertStacking("gridXxl", true, 380);
		assertStacking("gridLg",  true, 380);

		resize(1500);
		assertStacking("gridXxl", false, 1500);
		assertStacking("gridLg",  false, 1500);
	}
}
