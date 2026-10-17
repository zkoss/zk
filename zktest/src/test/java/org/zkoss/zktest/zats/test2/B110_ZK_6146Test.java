/* B110_ZK_6146Test.java

        Purpose:

        Description:

        History:
                Wed Aug 19 10:12:03 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.zkoss.test.webdriver.WebDriverTestCase;

public class B110_ZK_6146Test extends WebDriverTestCase {

	private static final String PAGE = "/test2/B110-ZK-6146.zul";

	private JavascriptExecutor js() {
		return (JavascriptExecutor) driver;
	}

	private void openPopup(String boxId) {
		click(jq(boxId + " .z-daterangebox-button"));
		waitResponse();
	}

	private void clickCell(String boxId, int day) {
		WebElement cell = (WebElement) js().executeScript(
				"var p = zk.Widget.$(arguments[0])._rangePopup;"
				+ "var pane = p.$n('panels').querySelector('.z-calendar');"
				+ "var cells = pane.querySelectorAll('td.z-calendar-cell');"
				+ "for (var i = 0; i < cells.length; i++)"
				+ "  if (jq(cells[i]).data('value') === arguments[1] && (cells[i]._monofs || 0) === 0)"
				+ "    return cells[i];",
				boxId, day);
		assertTrue(cell != null, "The requested day must be visible in the first calendar");
		getActions().moveToElement(cell).click().perform();
		waitResponse();
	}

	private void completeRange(String boxId) {
		openPopup(boxId);
		clickCell(boxId, 10);
		clickCell(boxId, 15);
		sleep(300); // ZK-6146: allow the date-only auto-apply timer to fire.
		waitResponse();
	}

	private String displayedMonth(String boxId) {
		return (String) js().executeScript(
				"return zk.fmt.Date.formatDate(zk.Widget.$(arguments[0])._rangePopup._panels[0].getValue(), 'yyyy-MM');",
				boxId);
	}

	private String popupDisplay(String boxId) {
		return (String) js().executeScript(
				"return zk.Widget.$(arguments[0])._rangePopup.$n().style.display;", boxId);
	}

	private String inputValue(String boxId, String side) {
		return (String) js().executeScript(
				"return jq(arguments[0] + ' .z-daterangebox-' + arguments[1])[0].value;",
				boxId, side);
	}

	private WebElement spinner(String side) {
		return (WebElement) js().executeScript(
				"var p = zk.Widget.$('$drTime')._rangePopup;"
				+ "return (arguments[0] === 'begin' ? p._beginTime : p._endTime).getInputNode();", side);
	}

	private String spinnerValue(String side) {
		return spinner(side).getAttribute("value");
	}

	private void editSpinnerHour(String side, Keys key) {
		getActions().click(spinner(side)).sendKeys(Keys.HOME, key).perform();
		waitResponse();
	}

	private long visibleSpinnerCount() {
		return (Long) js().executeScript(
				"var inps = zk.Widget.$('$drTime')._rangePopup.$n('times').querySelectorAll('input.z-timebox-input'), n = 0;"
				+ "for (var i = 0; i < inps.length; i++) if (inps[i].offsetParent) n++;"
				+ "return n;");
	}

	private String spinnerName(String side) {
		return (String) js().executeScript(
				"var p = zk.Widget.$('$drTime')._rangePopup;"
				+ "var n = (arguments[0] === 'begin' ? p._beginTime : p._endTime).getInputNode();"
				+ "return n.getAttribute('aria-label') || n.getAttribute('aria-labelledby') || '';", side);
	}

	private void assertTimeRangeUncommitted() {
		assertEquals("0", jq("$countTime").text(), "Popup edits must not send onChange before dismissal");
		assertEquals("", jq("$lblTime").text(), "The server must keep its opening range");
		assertEquals("", inputValue("$drTime", "begin"));
		assertEquals("", inputValue("$drTime", "end"));
		assertNotEquals("none", popupDisplay("$drTime"));
		assertFalse(jq(".z-errorbox").exists(), "Staged edits must not be validated individually");
	}

	@Test
	public void testShowTimeStagesDatesAndSpinnerBlurUntilOutsideClick() {
		connect(PAGE);
		completeRange("$drTime");
		String month = displayedMonth("$drTime");
		assertTimeRangeUncommitted();
		assertEquals(2L, visibleSpinnerCount());

		editSpinnerHour("begin", Keys.ARROW_UP);
		assertEquals("01:00", spinnerValue("begin"));
		editSpinnerHour("end", Keys.ARROW_DOWN);
		assertEquals("22:59", spinnerValue("end"));
		getActions().click(spinner("begin")).perform();
		waitResponse();
		assertTimeRangeUncommitted();

		click(jq("$lblTitle"));
		waitResponse();
		String begin = month + "-10 01:00";
		String end = month + "-15 22:59";
		assertEquals("none", popupDisplay("$drTime"));
		assertEquals("1", jq("$countTime").text(), "An outside click must commit the combined range once");
		assertEquals(begin + "|" + end, jq("$lblTime").text());
		assertEquals(begin, inputValue("$drTime", "begin"));
		assertEquals(end, inputValue("$drTime", "end"));

		openPopup("$drTime");
		assertEquals("01:00", spinnerValue("begin"));
		assertEquals("22:59", spinnerValue("end"));
		getActions().sendKeys(Keys.ESCAPE).perform();
		waitResponse();
		assertEquals("1", jq("$countTime").text(), "An unchanged session must not send another onChange");
	}

	@Test
	public void testShowTimeOutsideClickCommitsDatesAndUnblurredArrowEditOnce() {
		connect(PAGE);
		completeRange("$drTime");
		String month = displayedMonth("$drTime");
		assertTimeRangeUncommitted();

		getActions().click(spinner("end")).sendKeys(Keys.END).perform();
		String uuid = (String) js().executeScript("return zk.Widget.$('$drTime')._rangePopup._endTime.uuid;");
		WebElement up = driver.findElement(By.id(uuid + "-btn-up"));
		getActions().moveToElement(up).click().perform();
		waitResponse();
		String edited = spinnerValue("end");
		assertNotEquals("23:59", edited, "The spinner arrow must change the visible end time");
		assertTimeRangeUncommitted();

		click(jq("$lblTitle"));
		waitResponse();
		assertEquals("none", popupDisplay("$drTime"));
		assertEquals("1", jq("$countTime").text(), "Outside dismissal must submit the combined range once");
		assertEquals(month + "-10 00:00|" + month + "-15 " + edited, jq("$lblTime").text());
		assertEquals(month + "-15 " + edited, inputValue("$drTime", "end"));
		openPopup("$drTime");
		assertEquals(edited, spinnerValue("end"));
		assertEquals("1", jq("$countTime").text());
	}

	@Test
	public void testShowTimeCancelDiscardsNewDatesAndTimes() {
		connect(PAGE);
		completeRange("$drTime");
		editSpinnerHour("begin", Keys.ARROW_UP);
		getActions().click(spinner("end")).perform();
		waitResponse();
		assertTimeRangeUncommitted();

		click(jq(".z-daterangebox-popup-cancel"));
		waitResponse();
		assertEquals("none", popupDisplay("$drTime"));
		assertEquals("0", jq("$countTime").text());
		assertEquals("", jq("$lblTime").text());
		assertEquals("", inputValue("$drTime", "begin"));
		assertEquals("", inputValue("$drTime", "end"));
		openPopup("$drTime");
		assertEquals("00:00", spinnerValue("begin"));
		assertEquals("23:59", spinnerValue("end"));
		assertEquals(0, jq(".z-cell-range-begin").length());
		assertEquals(0, jq(".z-cell-range-end").length());
	}

	@Test
	public void testShowTimeSpinnersHaveDistinctAccessibleNames() {
		connect(PAGE);
		if (!Boolean.valueOf(getEval("!!window.za11y")))
			return;

		openPopup("$drTime");
		assertEquals(2L, visibleSpinnerCount());
		String begin = spinnerName("begin");
		String end = spinnerName("end");
		assertFalse(begin.isEmpty(), "The begin time spinner needs an accessible name");
		assertFalse(end.isEmpty(), "The end time spinner needs an accessible name");
		assertFalse(begin.startsWith("Unknown message code"));
		assertFalse(end.startsWith("Unknown message code"));
		assertNotEquals(begin, end, "The two spinners must have distinct accessible names");
	}

	@Test
	public void testDateOnlyStillAutoAppliesAndCloses() {
		connect(PAGE);
		completeRange("$drDate");
		String month = displayedMonth("$drDate");
		assertEquals("none", popupDisplay("$drDate"));
		assertEquals("1", jq("$countDate").text(), "Date-only selection must still auto-apply once");
		assertEquals(month + "-10|" + month + "-15", jq("$lblDate").text());
		assertEquals(month + "-10", inputValue("$drDate", "begin"));
		assertEquals(month + "-15", inputValue("$drDate", "end"));
	}
}
