/* F104_ZK_5409_ResponsiveGridContainerTest.java

        Purpose:
                
        Description:
                
        History:
                Sat Apr 25 22:23:40 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.Dimension;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class F104_ZK_5409_ResponsiveGridContainerTest extends WebDriverTestCase {

	/**
	 * Test 11.2: Grid inside a Window — container width follows the Window's content width.
	 */
	@Test
	public void testGridInWindow() {
		connect("/test2/F104-ZK-5409-responsive-grid-container.zul");
		waitResponse();

		Dimension size = driver.manage().window().getSize();

		// Wide window: grid in window should be in table mode
		driver.manage().window().setSize(new Dimension(1200, size.height));
		waitResponse();

		assertFalse(jq("$gridInWindow").hasClass("z-grid--stacking"),
				"Grid in Window should be table mode at wide width");

		// Narrow window: grid enters stacking
		driver.manage().window().setSize(new Dimension(600, size.height));
		waitResponse();

		assertTrue(jq("$gridInWindow").hasClass("z-grid--stacking"),
				"Grid in Window should enter stacking at narrow width");

		// Stacking zone should contain the rows
		assertTrue(jq("$gridInWindow").find(".z-grid-body tbody > tr.z-row").length() >= 2,
				"Stacking zone should contain 2 rows");

		assertNoJSError();
	}

	/**
	 * Test 11.10: Grid inside a Groupbox — responsive to the Groupbox content width.
	 */
	@Test
	public void testGridInGroupbox() {
		connect("/test2/F104-ZK-5409-responsive-grid-container.zul");
		waitResponse();

		Dimension size = driver.manage().window().getSize();

		driver.manage().window().setSize(new Dimension(1200, size.height));
		waitResponse();

		assertFalse(jq("$gridInGroupbox").hasClass("z-grid--stacking"),
				"Grid in Groupbox should be table mode at wide width");

		driver.manage().window().setSize(new Dimension(600, size.height));
		waitResponse();

		assertTrue(jq("$gridInGroupbox").hasClass("z-grid--stacking"),
				"Grid in Groupbox should enter stacking at narrow width");

		assertNoJSError();
	}

	/**
	 * Test 11.3: Grid inside the initially visible Tabpanel.
	 */
	@Test
	public void testGridInVisibleTab() {
		connect("/test2/F104-ZK-5409-responsive-grid-container.zul");
		waitResponse();

		Dimension size = driver.manage().window().getSize();

		driver.manage().window().setSize(new Dimension(600, size.height));
		waitResponse();

		// grid in first tab (visible on load) should enter stacking
		assertTrue(jq("$gridInTab1").hasClass("z-grid--stacking"),
				"Grid in initially visible tab should enter stacking at narrow width");
	}

	/**
	 * Test 11.4: Grid inside an initially hidden Tabpanel — should enter stacking
	 * when the tab becomes visible (ResizeObserver should fire when display changes).
	 */
	@Test
	public void testGridInHiddenTab() {
		connect("/test2/F104-ZK-5409-responsive-grid-container.zul");
		waitResponse();

		Dimension size = driver.manage().window().getSize();

		driver.manage().window().setSize(new Dimension(600, size.height));
		waitResponse();

		// Click the second tab to make it visible
		click(jq("$tabboxContainer .z-tab:eq(1)"));
		waitResponse();

		// Grid in the newly-visible tab should be in stacking mode
		assertTrue(jq("$gridInTab2").hasClass("z-grid--stacking"),
				"Grid in newly-visible tab should enter stacking at narrow width");
	}

	/**
	 * Test 11.7: Grid in a narrow sidebar-like container — forces stacking
	 * regardless of window width (since container width < breakpoint).
	 */
	@Test
	public void testGridInNarrowSidebar() {
		connect("/test2/F104-ZK-5409-responsive-grid-container.zul");
		waitResponse();

		Dimension size = driver.manage().window().getSize();

		// Even with a wide window, the 300px sidebar should force stacking
		driver.manage().window().setSize(new Dimension(1400, size.height));
		waitResponse();

		assertTrue(jq("$gridInSidebar").hasClass("z-grid--stacking"),
				"Grid in 300px sidebar should always be in stacking mode (300 < 768 md breakpoint)");

		// Stacking zone content should be present
		assertTrue(jq("$gridInSidebar").find(".z-grid-body tbody > tr.z-row").length() >= 2,
				"Narrow sidebar grid should have stacking rows");
	}

	/**
	 * Test 11.5: Grid inside a Popup — opens popup and verifies stacking applies
	 * based on popup's content width.
	 */
	@Test
	public void testGridInPopup() {
		connect("/test2/F104-ZK-5409-responsive-grid-container.zul");
		waitResponse();

		Dimension size = driver.manage().window().getSize();
		driver.manage().window().setSize(new Dimension(1200, size.height));
		waitResponse();

		// Open the popup
		click(jq("$btnShowPopup"));
		waitResponse();
		waitResponse();

		// Popup is 400px wide, so grid inside should be in stacking mode
		// (400 < 768 md breakpoint)
		assertTrue(jq("$gridInPopup").hasClass("z-grid--stacking"),
				"Grid in 400px popup should be in stacking mode");

		// Stacking zone should be populated
		assertTrue(jq("$gridInPopup").find(".z-grid-body tbody > tr.z-row").length() >= 2,
				"Grid in popup should have stacking rows");

		assertNoJSError();
	}
}
