/* B110_ZK_6214Test.java

        Purpose:

        Description:

        History:
                Mon Oct 19 14:40:00 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.WheelInput;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B110_ZK_6214Test extends WebDriverTestCase {

	@Override
	protected ChromeOptions getWebDriverOptions() {
		return super.getWebDriverOptions().addArguments("--accept-lang=en-US");
	}

	private String panel(int index) {
		return ".z-daterangebox-popup:visible .z-calendar:eq(" + index + ")";
	}

	private void openPopup(String box, int panels) {
		connect();
		waitResponse();
		click(jq("$" + box + " .z-daterangebox-button"));
		waitResponse();
		assertEquals(panels, jq(".z-daterangebox-popup:visible .z-calendar").length());
		assertRangeUnchanged(box);
	}

	private void clickInPanel(int index, String selector) {
		String target = panel(index) + " " + selector;
		assertTrue(jq(target).exists(), "calendar navigation target must exist: " + target);
		click(jq(target));
		waitResponse();
	}

	private void wheelInPanel(int index, int deltaY) {
		getActions().scrollFromOrigin(WheelInput.ScrollOrigin.fromElement(toElement(jq(panel(index)))), 0, deltaY)
				.perform();
		waitResponse();
	}

	private void pressKeys(CharSequence... keys) {
		getActions().sendKeys(keys).perform();
		waitResponse();
	}

	private void assertMonths(String... titles) {
		for (int i = 0; i < titles.length; i++)
			assertEquals(titles[i], jq(panel(i) + " .z-calendar-title").text().trim(),
					"panel " + i + " must keep its position among consecutive months");
	}

	private void assertRangeUnchanged(String box) {
		assertEquals("2026-10-10", jq("$" + box + " .z-daterangebox-begin").val());
		assertEquals("2026-10-20", jq("$" + box + " .z-daterangebox-end").val());
		assertEquals("0", jq("$changes").text(), "navigation must not fire onChange");
		assertEquals("2026-10-10/2026-10-20", getEval("(function () {"
				+ "var p = zk.Widget.$('$" + box + "')._rangePopup;"
				+ "return [p._stagedBegin, p._stagedEnd].map(function (d) {"
				+ "return zk.fmt.Date.formatDate(Dates.newInstance(d.getTime(), 'Asia/Taipei'), 'yyyy-MM-dd');"
				+ "}).join('/');})()"), "navigation must keep the staged range");
	}

	@Test
	public void leftMonthJumpKeepsNextPanelConsecutive() {
		openPopup("twoMonths", 2);
		assertMonths("Oct 2026", "Nov 2026");

		clickInPanel(0, "[id$=\"-tm\"]");
		clickInPanel(0, ".z-calendar-month [data-value=\"11\"]");

		assertMonths("Dec 2026", "Jan 2027");
		assertRangeUnchanged("twoMonths");
	}

	@Test
	public void rightMonthJumpKeepsPreviousPanelConsecutive() {
		openPopup("twoMonths", 2);

		clickInPanel(1, "[id$=\"-tm\"]");
		clickInPanel(1, ".z-calendar-month [data-value=\"0\"]");

		assertMonths("Dec 2025", "Jan 2026");
		assertRangeUnchanged("twoMonths");
	}

	@Test
	public void middleYearAndMonthJumpKeepsAllPanelsConsecutive() {
		openPopup("threeMonths", 3);
		assertMonths("Oct 2026", "Nov 2026", "Dec 2026");

		clickInPanel(1, "[id$=\"-ty\"]");
		clickInPanel(1, ".z-calendar-year [data-value=\"2027\"]");
		assertRangeUnchanged("threeMonths");
		clickInPanel(1, ".z-calendar-month [data-value=\"0\"]");

		assertMonths("Dec 2026", "Jan 2027", "Feb 2027");
		assertRangeUnchanged("threeMonths");
	}

	@Test
	public void arrowOntoDisabledFirstKeepsPanelsConsecutive() {
		openPopup("constrained", 2);
		assertMonths("Oct 2026", "Nov 2026");

		clickInPanel(1, ".z-calendar-right");
		assertMonths("Nov 2026", "Dec 2026");

		clickInPanel(0, ".z-calendar-left");
		assertMonths("Oct 2026", "Nov 2026");

		clickInPanel(0, ".z-calendar-left");
		assertMonths("Sep 2026", "Oct 2026");
		assertRangeUnchanged("constrained");
		assertEquals("10", jq(panel(1) + " .z-cell-range-begin").text().trim(),
				"the moved panels must repaint the range highlight");
		assertEquals("20", jq(panel(1) + " .z-cell-range-end").text().trim(),
				"the moved panels must repaint the range highlight");
	}

	@Test
	public void wheelOntoDisabledFirstKeepsPanelsConsecutive() {
		openPopup("constrained", 2);

		wheelInPanel(1, 100);
		assertMonths("Nov 2026", "Dec 2026");

		wheelInPanel(0, -100);
		assertMonths("Oct 2026", "Nov 2026");

		wheelInPanel(0, -100);
		assertMonths("Sep 2026", "Oct 2026");
		assertRangeUnchanged("constrained");
	}

	@Test
	public void keyboardMonthPickKeepsNextPanelConsecutive() {
		openPopup("twoMonths", 2);

		clickInPanel(0, "[id$=\"-tm\"]");
		pressKeys(Keys.ARROW_RIGHT, Keys.ARROW_RIGHT, Keys.ENTER);

		assertMonths("Dec 2026", "Jan 2027");
		assertRangeUnchanged("twoMonths");
	}

	@Test
	public void keyboardYearAndMonthPickKeepsAllPanelsConsecutive() {
		openPopup("threeMonths", 3);

		clickInPanel(1, "[id$=\"-ty\"]");
		pressKeys(Keys.ARROW_RIGHT, Keys.ENTER);
		pressKeys(Keys.ARROW_UP, Keys.ARROW_UP, Keys.ARROW_LEFT, Keys.ARROW_LEFT, Keys.ENTER);

		assertMonths("Dec 2026", "Jan 2027", "Feb 2027");
		assertRangeUnchanged("threeMonths");
	}
}
