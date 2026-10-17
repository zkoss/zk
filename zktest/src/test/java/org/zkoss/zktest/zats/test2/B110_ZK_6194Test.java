/* B110_ZK_6194Test.java

        Purpose:

        Description:

        History:
                Fri Oct 16 15:30:00 CST 2026, Created by peakerlee

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

public class B110_ZK_6194Test extends WebDriverTestCase {

	private static final String BEGIN = "2026-08-07 09:00";
	private static final String END = "2026-08-07 10:00";

	private JavascriptExecutor js() {
		return (JavascriptExecutor) driver;
	}

	private void openPopup() {
		click(jq("$d .z-daterangebox-button"));
		waitResponse();
	}

	private void pickDay(int day) {
		WebElement cell = (WebElement) js().executeScript(
				"var pane = zk.Widget.$('$d')._rangePopup.$n('panels').querySelector('.z-calendar');"
				+ "var cells = pane.querySelectorAll('td.z-calendar-cell');"
				+ "for (var i = 0; i < cells.length; i++)"
				+ "  if (jq(cells[i]).data('value') === arguments[0] && (cells[i]._monofs || 0) === 0)"
				+ "    return cells[i];", day);
		assertTrue(cell != null, "The requested day must be visible in the first calendar");
		getActions().moveToElement(cell).click().perform();
		waitResponse();
	}

	private String popupDisplay() {
		return getEval("zk.Widget.$('$d')._rangePopup.$n().style.display");
	}

	private String inputValue(String side) {
		return (String) js().executeScript("return jq('$d .z-daterangebox-' + arguments[0])[0].value;", side);
	}

	private WebElement spinner(String side) {
		return (WebElement) js().executeScript(
				"var p = zk.Widget.$('$d')._rangePopup;"
				+ "return (arguments[0] === 'begin' ? p._beginTime : p._endTime).getInputNode();", side);
	}

	private void advanceHour(String side, int hours) {
		getActions().click(spinner(side)).sendKeys(Keys.HOME).perform();
		for (int i = 0; i < Math.abs(hours); i++)
			getActions().sendKeys(hours < 0 ? Keys.ARROW_DOWN : Keys.ARROW_UP).perform();
		waitResponse();
	}

	private void typeOuterInput(String side, String value) {
		WebElement input = driver.findElement(By.cssSelector(".z-daterangebox-" + side));
		getActions().click(input).perform();
		waitResponse();
		js().executeScript("arguments[0].select();", input);
		getActions().sendKeys(value.isEmpty() ? Keys.BACK_SPACE : value).perform();
		getActions().click(driver.findElement(By.cssSelector(
				".z-daterangebox-" + ("begin".equals(side) ? "end" : "begin")))).perform();
		waitResponse();
	}

	private void stageBeginTimeAndClearEnd() {
		connect();
		openPopup();
		advanceHour("begin", -1);
		typeOuterInput("end", "");
		assertNotEquals("none", popupDisplay());
		assertEquals("08:00", spinner("begin").getAttribute("value"));
		assertEquals("1", jq("$count").text(), "Clearing the outer input commits independently");
		assertEquals(BEGIN + "|", jq("$serverRange").text());
	}

	private void assertOpeningRangeUnchanged() {
		assertEquals("0", jq("$count").text(), "Staged changes must not reach the server");
		assertEquals(BEGIN + "|" + END, jq("$serverRange").text());
		assertEquals(BEGIN, inputValue("begin"));
		assertEquals(END, inputValue("end"));
		assertFalse(jq(".z-errorbox").exists(), "Incomplete time edits must not cause a range error");
	}

	private void assertDiscardedAndReopen() {
		assertEquals("none", popupDisplay());
		assertOpeningRangeUnchanged();
		openPopup();
		assertEquals("09:00", spinner("begin").getAttribute("value"));
		assertEquals("10:00", spinner("end").getAttribute("value"));
		assertEquals(BEGIN + "|" + END, getEval(
				"(function () {var p = zk.Widget.$('$d')._rangePopup;"
				+ "return zk.fmt.Date.formatDate(Dates.newInstance(p._stagedBegin.getTime()), 'yyyy-MM-dd HH:mm') + '|'"
				+ " + zk.fmt.Date.formatDate(Dates.newInstance(p._stagedEnd.getTime()), 'yyyy-MM-dd HH:mm');})()"));
		assertEquals("0", jq("$count").text(), "Reopening after Cancel must not submit discarded edits");
	}

	private void listenForReentrantClose(boolean detach) {
		js().executeScript(
				"var box = zk.Widget.$('$d');"
				+ "window.zk6194Close = {changes: 0, closes: 0, errors: []};"
				+ "window.addEventListener('error', function (event) { window.zk6194Close.errors.push(event.message); });"
				+ "box.listen({onOpen: function (event) { if (!event.data.open) ++window.zk6194Close.closes; },"
				+ "onChange: function () { ++window.zk6194Close.changes;"
				+ (detach ? "box.detach();" : "box.setOpen(false);")
				+ "}});");
	}

	@Test
	public void testCancelDiscardsChangedDatesAndBothTimes() {
		connect();
		openPopup();
		pickDay(10);
		pickDay(15);
		sleep(300);
		waitResponse();
		advanceHour("begin", 2);
		advanceHour("end", 2);
		assertEquals("11:00", spinner("begin").getAttribute("value"));
		assertEquals("12:00", spinner("end").getAttribute("value"));
		assertOpeningRangeUnchanged();

		getActions().click(spinner("end")).sendKeys(Keys.END).perform();
		String uuid = getEval("zk.Widget.$('$d')._rangePopup._endTime.uuid");
		getActions().moveToElement(driver.findElement(By.id(uuid + "-btn-up"))).click().perform();
		waitResponse();
		assertEquals("12:01", spinner("end").getAttribute("value"));
		click(jq(".z-daterangebox-popup-cancel"));
		waitResponse();
		assertDiscardedAndReopen();
	}

	@Test
	public void testEscapeDiscardsChangedDatesAndBothTimes() {
		connect();
		openPopup();
		pickDay(10);
		pickDay(15);
		sleep(300);
		waitResponse();
		advanceHour("begin", 2);
		getActions().click(spinner("end")).sendKeys(Keys.END).perform();
		String uuid = getEval("zk.Widget.$('$d')._rangePopup._endTime.uuid");
		getActions().moveToElement(driver.findElement(By.id(uuid + "-btn-up"))).click().perform();
		waitResponse();
		assertEquals("10:01", spinner("end").getAttribute("value"));
		assertOpeningRangeUnchanged();
		getActions().sendKeys(Keys.ESCAPE).perform();
		waitResponse();
		assertDiscardedAndReopen();
	}

	@Test
	public void testTabToCancelDoesNotCommitSpinnerBlur() {
		connect();
		openPopup();
		advanceHour("end", 1);
		assertEquals("11:00", spinner("end").getAttribute("value"));
		getActions().sendKeys(Keys.TAB, Keys.TAB, Keys.TAB).perform();
		waitResponse();
		assertEquals("true", getEval("String(document.activeElement.classList.contains('z-daterangebox-popup-cancel'))"));
		assertOpeningRangeUnchanged();
		getActions().sendKeys(Keys.ENTER).perform();
		waitResponse();
		assertDiscardedAndReopen();
	}

	@Test
	public void testOutsideClickValidatesAndCommitsBothTimesTogether() {
		connect();
		openPopup();
		advanceHour("begin", 2);
		getActions().click(spinner("end")).perform();
		waitResponse();
		assertEquals("11:00", spinner("begin").getAttribute("value"));
		assertEquals("10:00", spinner("end").getAttribute("value"));
		assertOpeningRangeUnchanged();

		advanceHour("end", 2);
		click(jq("$lblTitle"));
		waitResponse();
		assertEquals("none", popupDisplay());
		assertFalse(jq(".z-errorbox").exists());
		assertEquals("1", jq("$count").text(), "An outside click must send one combined range change");
		assertEquals("2026-08-07 11:00|2026-08-07 12:00", jq("$serverRange").text());
		assertEquals("2026-08-07 11:00", inputValue("begin"));
		assertEquals("2026-08-07 12:00", inputValue("end"));
		openPopup();
		assertEquals("11:00", spinner("begin").getAttribute("value"));
		assertEquals("12:00", spinner("end").getAttribute("value"));
		assertEquals("1", jq("$count").text());
	}

	@Test
	public void testOutsideClickRejectsFinalReversedTimes() {
		connect();
		openPopup();
		advanceHour("begin", 2);
		getActions().click(spinner("end")).perform();
		waitResponse();
		assertEquals("11:00", spinner("begin").getAttribute("value"));
		assertEquals("10:00", spinner("end").getAttribute("value"));
		assertOpeningRangeUnchanged();

		click(jq("$lblTitle"));
		waitResponse();
		assertEquals("Begin date must not be later than end date", jq(".z-errorbox-content").text(),
				"The final reversed range must be validated on dismissal");
		assertEquals("0", jq("$count").text(), "An invalid combined range must not send onChange");
		assertEquals(BEGIN + "|" + END, jq("$serverRange").text());
		assertEquals("2026-08-07 11:00", inputValue("begin"));
		assertEquals(BEGIN, getEval("zk.fmt.Date.formatDate(Dates.newInstance(zk.Widget.$('$d').getBeginValueAsDate().getTime()), 'yyyy-MM-dd HH:mm')"));
		assertEquals(END, inputValue("end"));
	}

	@Test
	public void testTypingOneInputKeepsOtherStagedTime() {
		connect();
		openPopup();
		advanceHour("begin", 2);
		WebElement endInput = driver.findElement(By.cssSelector(".z-daterangebox-end"));
		getActions().click(endInput).perform();
		js().executeScript("arguments[0].select();", endInput);
		getActions().sendKeys("2026-08-07 12:30", Keys.ENTER).perform();
		waitResponse();
		assertNotEquals("none", popupDisplay());
		assertEquals("1", jq("$count").text());
		assertEquals(BEGIN + "|2026-08-07 12:30", jq("$serverRange").text());
		assertEquals("11:00", spinner("begin").getAttribute("value"), "Typing the end must keep the staged begin time");
		assertEquals("12:30", spinner("end").getAttribute("value"));

		click(jq("$lblTitle"));
		waitResponse();
		assertEquals("none", popupDisplay());
		assertEquals("2", jq("$count").text());
		assertEquals("2026-08-07 11:00|2026-08-07 12:30", jq("$serverRange").text());
	}

	@Test
	public void testTypingBeginAdoptsRejectedEndBeforeTimeChange() {
		connect();
		openPopup();
		typeOuterInput("end", "2026-08-06 10:00");
		assertNotEquals("none", popupDisplay());
		assertEquals("Begin date must not be later than end date", jq(".z-errorbox-content").text());
		assertEquals("0", jq("$count").text());
		assertEquals(BEGIN + "|" + END, jq("$serverRange").text());
		assertEquals("2026-08-06 10:00", inputValue("end"));

		typeOuterInput("begin", "2026-08-05 09:00");
		assertNotEquals("none", popupDisplay());
		assertFalse(jq(".z-errorbox").exists());
		assertEquals("2026-08-05 09:00|2026-08-06 10:00", jq("$serverRange").text());
		assertEquals("1", jq("$count").text());
		advanceHour("begin", -1);
		click(jq("$lblTitle"));
		waitResponse();
		assertEquals("none", popupDisplay());
		assertEquals("2026-08-05 08:00|2026-08-06 10:00", jq("$serverRange").text());
		assertEquals("2", jq("$count").text());
	}

	@Test
	public void testTypingEndAdoptsRejectedBeginBeforeTimeChange() {
		connect();
		openPopup();
		typeOuterInput("begin", "2026-08-08 09:00");
		assertNotEquals("none", popupDisplay());
		assertEquals("Begin date must not be later than end date", jq(".z-errorbox-content").text());
		assertEquals("0", jq("$count").text());
		assertEquals(BEGIN + "|" + END, jq("$serverRange").text());
		assertEquals("2026-08-08 09:00", inputValue("begin"));

		typeOuterInput("end", "2026-08-09 10:00");
		assertNotEquals("none", popupDisplay());
		assertFalse(jq(".z-errorbox").exists());
		assertEquals("2026-08-08 09:00|2026-08-09 10:00", jq("$serverRange").text());
		assertEquals("1", jq("$count").text());
		advanceHour("end", 1);
		click(jq("$lblTitle"));
		waitResponse();
		assertEquals("none", popupDisplay());
		assertEquals("2026-08-08 09:00|2026-08-09 11:00", jq("$serverRange").text());
		assertEquals("2", jq("$count").text());
	}

	@Test
	public void testOutsideClickKeepsStagedBeginTimeAfterEndIsCleared() {
		stageBeginTimeAndClearEnd();
		click(jq("$lblTitle"));
		waitResponse();
		assertEquals("none", popupDisplay());
		assertEquals("2026-08-07 08:00|", jq("$serverRange").text());
		assertEquals("2", jq("$count").text());
		assertEquals("2026-08-07 08:00", inputValue("begin"));
		assertEquals("", inputValue("end"));
	}

	@Test
	public void testOutsideClickKeepsStagedEndTimeAfterBeginIsCleared() {
		connect();
		openPopup();
		advanceHour("end", 1);
		typeOuterInput("begin", "");
		assertNotEquals("none", popupDisplay());
		assertEquals("11:00", spinner("end").getAttribute("value"));
		assertEquals("1", jq("$count").text(), "Clearing the outer input commits independently");
		assertEquals("|" + END, jq("$serverRange").text());
		click(jq("$lblTitle"));
		waitResponse();
		assertEquals("none", popupDisplay());
		assertEquals("|2026-08-07 11:00", jq("$serverRange").text());
		assertEquals("2", jq("$count").text());
		assertEquals("", inputValue("begin"));
		assertEquals("2026-08-07 11:00", inputValue("end"));
	}

	@Test
	public void testOutsideClickCommitsBeginTimeChangedAfterEndIsCleared() {
		connect();
		openPopup();
		typeOuterInput("end", "");
		assertEquals(BEGIN + "|", jq("$serverRange").text());
		advanceHour("begin", -1);
		click(jq("$lblTitle"));
		waitResponse();
		assertEquals("none", popupDisplay());
		assertEquals("2026-08-07 08:00|", jq("$serverRange").text());
		assertEquals("2", jq("$count").text());
	}

	@Test
	public void testOutsideClickCommitsEndTimeChangedAfterBeginIsCleared() {
		connect();
		openPopup();
		typeOuterInput("begin", "");
		assertEquals("|" + END, jq("$serverRange").text());
		advanceHour("end", 1);
		click(jq("$lblTitle"));
		waitResponse();
		assertEquals("none", popupDisplay());
		assertEquals("|2026-08-07 11:00", jq("$serverRange").text());
		assertEquals("2", jq("$count").text());
	}

	@Test
	public void testCancelDiscardsStagedTimeAfterEndIsCleared() {
		stageBeginTimeAndClearEnd();
		click(jq(".z-daterangebox-popup-cancel"));
		waitResponse();
		assertEquals("none", popupDisplay());
		assertEquals(BEGIN + "|", jq("$serverRange").text());
		assertEquals("1", jq("$count").text());
		openPopup();
		assertEquals("09:00", spinner("begin").getAttribute("value"));
		assertEquals("", inputValue("end"));
	}

	@Test
	public void testEscapeDiscardsStagedTimeAfterEndIsCleared() {
		stageBeginTimeAndClearEnd();
		getActions().sendKeys(Keys.ESCAPE).perform();
		waitResponse();
		assertEquals("none", popupDisplay());
		assertEquals(BEGIN + "|", jq("$serverRange").text());
		assertEquals("1", jq("$count").text());
		openPopup();
		assertEquals("09:00", spinner("begin").getAttribute("value"));
		assertEquals("", inputValue("end"));
	}

	@Test
	public void testOutsideClickDiscardsUnfinishedCalendarSelection() {
		connect();
		openPopup();
		pickDay(10);
		click(jq("$lblTitle"));
		waitResponse();
		assertEquals("none", popupDisplay());
		assertOpeningRangeUnchanged();
	}

	@Test
	public void testTypingBeginDoesNotCompleteCalendarSelection() {
		connect();
		openPopup();
		pickDay(10);
		typeOuterInput("begin", "2026-08-06 09:00");
		assertNotEquals("none", popupDisplay());
		assertEquals("2026-08-06 09:00|" + END, jq("$serverRange").text());
		assertEquals("1", jq("$count").text());
		click(jq("$lblTitle"));
		waitResponse();
		assertEquals("none", popupDisplay());
		assertEquals("2026-08-06 09:00|" + END, jq("$serverRange").text());
		assertEquals("1", jq("$count").text(), "An unfinished calendar selection must not clear the committed end");
		assertEquals(END, inputValue("end"));
	}

	@Test
	public void testClearingBeginDoesNotCompleteCalendarSelection() {
		connect();
		openPopup();
		pickDay(10);
		typeOuterInput("begin", "");
		assertNotEquals("none", popupDisplay());
		assertEquals("|" + END, jq("$serverRange").text());
		assertEquals("1", jq("$count").text());
		click(jq("$lblTitle"));
		waitResponse();
		assertEquals("none", popupDisplay());
		assertEquals("|" + END, jq("$serverRange").text());
		assertEquals("1", jq("$count").text(), "Clearing begin must not also clear the unselected end");
		assertEquals(END, inputValue("end"));
	}

	@Test
	public void testTypingEndCompletesCalendarSelection() {
		connect();
		openPopup();
		pickDay(10);
		typeOuterInput("end", "2026-08-15 12:30");
		assertNotEquals("none", popupDisplay());
		assertEquals(BEGIN + "|2026-08-15 12:30", jq("$serverRange").text());
		assertEquals("1", jq("$count").text());
		click(jq("$lblTitle"));
		waitResponse();
		assertEquals("none", popupDisplay());
		assertEquals("2026-08-10 09:00|2026-08-15 12:30", jq("$serverRange").text());
		assertEquals("2", jq("$count").text());
	}

	@Test
	public void testClearingEndCompletesCalendarSelection() {
		connect();
		openPopup();
		pickDay(10);
		typeOuterInput("end", "");
		assertNotEquals("none", popupDisplay());
		assertEquals(BEGIN + "|", jq("$serverRange").text());
		assertEquals("1", jq("$count").text());
		click(jq("$lblTitle"));
		waitResponse();
		assertEquals("none", popupDisplay());
		assertEquals("2026-08-10 09:00|", jq("$serverRange").text());
		assertEquals("2", jq("$count").text());
	}

	@Test
	public void testClearReplacesUnfinishedCalendarSelection() {
		connect();
		openPopup();
		pickDay(10);
		click(jq(".z-daterangebox-popup-clear"));
		waitResponse();
		assertOpeningRangeUnchanged();
		click(jq("$lblTitle"));
		waitResponse();
		assertEquals("none", popupDisplay());
		assertEquals("|", jq("$serverRange").text());
		assertEquals("1", jq("$count").text());
	}

	@Test
	public void testTodayReplacesUnfinishedCalendarSelection() {
		connect();
		openPopup();
		pickDay(10);
		click(jq(".z-daterangebox-popup-today"));
		waitResponse();
		assertOpeningRangeUnchanged();
		String today = getEval("zk.fmt.Date.formatDate(zUtl.today(false), 'yyyy-MM-dd')");
		click(jq("$lblTitle"));
		waitResponse();
		assertEquals("none", popupDisplay());
		assertEquals(today + " 09:00|" + today + " 10:00", jq("$serverRange").text());
		assertEquals("1", jq("$count").text());
	}

	@Test
	public void testServerCloseDiscardsStagedDatesAndTimes() {
		connect();
		openPopup();
		pickDay(10);
		pickDay(15);
		sleep(300);
		waitResponse();
		advanceHour("begin", 2);
		getActions().click(spinner("end")).perform();
		waitResponse();
		assertOpeningRangeUnchanged();
		js().executeScript("zk.Widget.$('$d').setOpen(false, true);");
		waitResponse();
		assertDiscardedAndReopen();
	}

	@Test
	public void testOutsideClickCommitsRangeStagedBeforeShowTimeTurnsOff() {
		connect();
		openPopup();
		pickDay(10);
		pickDay(15);
		sleep(300);
		waitResponse();
		assertOpeningRangeUnchanged();
		js().executeScript("zk.Widget.$('$d').setShowTime(false);");
		click(jq("$lblTitle"));
		waitResponse();
		assertEquals("none", popupDisplay());
		assertEquals("1", jq("$count").text(), "The staged range must still commit on dismissal");
		assertTrue(jq("$serverRange").text().matches("2026-08-10 \\d\\d:\\d\\d\\|2026-08-15 \\d\\d:\\d\\d"),
				jq("$serverRange").text());
	}

	@Test
	public void testOutsideClickCommitAllowsChangeListenerToDetach() {
		connect();
		openPopup();
		advanceHour("end", 1);
		assertOpeningRangeUnchanged();
		listenForReentrantClose(true);

		click(jq("$lblTitle"));
		waitResponse();
		assertEquals("1", getEval("String(window.zk6194Close.changes)"), "The synchronous onChange listener must run once");
		assertFalse(jq("$d").exists(), "The onChange listener must detach the box");
		assertFalse(jq(".z-daterangebox-popup").exists(), "Detaching the box must release its popup");
		assertEquals("[]", getEval("JSON.stringify(window.zk6194Close.errors)"), "Closing must survive a listener detaching the box");
	}

	@Test
	public void testOutsideClickCommitWithListenerCloseFiresOneCloseEvent() {
		connect();
		openPopup();
		advanceHour("end", 1);
		assertOpeningRangeUnchanged();
		listenForReentrantClose(false);

		click(jq("$lblTitle"));
		waitResponse();
		assertEquals("1", getEval("String(window.zk6194Close.changes)"), "The synchronous onChange listener must run once");
		assertEquals("1", getEval("String(window.zk6194Close.closes)"), "A reentrant close must fire onOpen:false once");
		assertEquals("none", popupDisplay());
		assertEquals("1", jq("$count").text());
		assertEquals(BEGIN + "|2026-08-07 11:00", jq("$serverRange").text());
		assertEquals("[]", getEval("JSON.stringify(window.zk6194Close.errors)"));
	}

	@Test
	public void testCancelDiscardsClear() {
		connect();
		openPopup();
		click(jq(".z-daterangebox-popup-clear"));
		waitResponse();
		assertNotEquals("none", popupDisplay());
		assertEquals(0, jq(".z-cell-range-begin").length());
		assertEquals(0, jq(".z-cell-range-end").length());
		assertOpeningRangeUnchanged();
		click(jq(".z-daterangebox-popup-cancel"));
		waitResponse();
		assertDiscardedAndReopen();
	}

	@Test
	public void testCancelDiscardsToday() {
		connect();
		openPopup();
		click(jq(".z-daterangebox-popup-today"));
		waitResponse();
		assertNotEquals("none", popupDisplay());
		assertEquals(getEval("zk.fmt.Date.formatDate(zUtl.today(false), 'yyyy-MM-dd')"),
				getEval("zk.fmt.Date.formatDate(Dates.newInstance(zk.Widget.$('$d')._rangePopup._stagedBegin.getTime()), 'yyyy-MM-dd')"));
		assertOpeningRangeUnchanged();
		click(jq(".z-daterangebox-popup-cancel"));
		waitResponse();
		assertDiscardedAndReopen();
	}
}
