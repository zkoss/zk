/* F104_ZK_4305_DaterangeAttributeTest.java

        Purpose:
                
        Description:
                
        History:
                Fri May 08 15:14:16 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.zkoss.test.webdriver.WebDriverTestCase;

public class F104_ZK_4305_DaterangeAttributeTest extends WebDriverTestCase {

	private JavascriptExecutor js() {
		return (JavascriptExecutor) driver;
	}

	/** True when the EE za11y a11y layer is active on the client. The default
	 *  ARIA labels (group/open/begin/end) live in the za11y tier (msgza11y) and
	 *  are injected by the zkmax/db-a11y augment only when za11y is loaded; the
	 *  app-supplied ca:aria-* path works regardless. */
	private boolean za11yActive() {
		return Boolean.TRUE.equals(js().executeScript("return !!window.za11y;"));
	}

	/** Poll up to 2s for the script expression to return a truthy value.
	 *  The expression is wrapped in `return (...)` so caller passes a
	 *  bare JS expression (no trailing semicolon). Use this instead of a
	 *  fixed sleep() so slow CI agents don't trip the 200ms range-completion
	 *  debounce or any other internal animation timing. */
	private void waitForJs(String exprReturningBoolean) {
		new WebDriverWait(driver, Duration.ofSeconds(2)).until(d ->
				Boolean.TRUE.equals(js().executeScript("return (" + exprReturningBoolean + ");")));
	}

	/** Wait until the floating popup is hidden (closed). The popup is
	 *  reparented to <body>, so a single querySelector matches whichever
	 *  popup the test interacted with. */
	private void waitForPopupClosed() {
		waitForJs("(function(){var p=document.querySelector('.z-daterangebox-popup');"
				+ "return !p || p.style.display==='none';})()");
	}

	/** Wait until the begin input of the given box has a non-empty value
	 *  (i.e. an apply has populated it). */
	private void waitForBeginInputPopulated(String boxIdSelector) {
		waitForJs("(function(){var v=jq('" + boxIdSelector
				+ " .z-daterangebox-begin')[0].value;return v && v.length>0;})()");
	}

	private void clickCellInPanel(String boxId, int panelIndex, int day) {
		// Day cells store their value in jQuery's data cache, not in a DOM
		// attribute, so we look it up via jq().data('value'). The popup is
		// reparented to <body>, so we resolve the popup via the box's widget.
		js().executeScript(
				"var box = zk.Widget.$(jq('" + boxId + "')[0]);"
				+ "var popup = box._rangePopup;"
				+ "if (!popup || !popup.$n()) return;"
				+ "var panes = popup.$n().querySelectorAll('.z-calendar');"
				+ "var pane = panes[arguments[0]];"
				+ "if (!pane) return;"
				+ "var cells = pane.querySelectorAll('td.z-calendar-cell');"
				+ "for (var i=0;i<cells.length;i++) {"
				+ "  var v = jq(cells[i]).data('value');"
				+ "  if (v === arguments[1] && (cells[i]._monofs||0) === 0) {"
				+ "    var w = zk.Widget.$(pane);"
				+ "    w._clickDate({target: cells[i], domTarget: cells[i], stop: function(){}});"
				+ "    return;"
				+ "  }"
				+ "}",
				panelIndex, day);
		waitResponse();
	}

	// ===== D1 boolean =====

	/** D1+E: disabled=true must block popup from opening on icon click. */
	@Test
	public void testDisabledBlocksPopupOpen() {
		connect("/test2/F104-ZK-4305-attributes.zul");
		waitResponse();

		click(jq("$drDisabled .z-daterangebox-button"));
		waitResponse();

		Boolean opened = (Boolean) js().executeScript(
				"return zk.Widget.$(jq('$drDisabled')[0])._open === true;");
		assertFalse(opened, "Disabled daterangebox must not open popup on click");
	}

	/** D1: buttonVisible=false suppresses the calendar icon entirely. */
	@Test
	public void testButtonVisibleFalseHidesIcon() {
		connect("/test2/F104-ZK-4305-attributes.zul");
		waitResponse();

		assertFalse(jq("$drNoButton .z-daterangebox-button").exists(),
				"buttonVisible=false must not render the calendar icon");
		// Inputs must still render.
		assertTrue(jq("$drNoButton .z-daterangebox-begin").exists(),
				"begin input still renders without the icon");
		assertTrue(jq("$drNoButton .z-daterangebox-end").exists(),
				"end input still renders without the icon");
	}

	// ===== D3 numeric =====

	/** D3: numberOfMonths=3 renders three side-by-side calendar panels. */
	@Test
	public void testNumberOfMonthsThreeRendersThreePanels() {
		connect("/test2/F104-ZK-4305-attributes.zul");
		waitResponse();

		click(jq("$drNumMonths .z-daterangebox-button"));
		waitResponse();

		Long panelCount = (Long) js().executeScript(
				"var box = zk.Widget.$(jq('$drNumMonths')[0]);"
				+ "return box._rangePopup.$n().querySelectorAll('.z-daterangebox-popup-panels .z-calendar').length;");
		assertEquals(3L, panelCount.longValue(),
				"numberOfMonths=3 must render 3 calendar panels");
	}

	// ===== D2 string + apply =====

	/** D2 + apply: format='yyyy-MM-dd' must be honoured when the committed
	 *  range is rendered back into the begin/end inputs. */
	@Test
	public void testFormatAttributeAffectsInputValue() {
		connect("/test2/F104-ZK-4305-attributes.zul");
		waitResponse();

		click(jq("$drFormat .z-daterangebox-button"));
		waitResponse();
		clickCellInPanel("$drFormat", 0, 10);
		clickCellInPanel("$drFormat", 0, 15);
		waitForBeginInputPopulated("$drFormat");
		waitResponse();

		String beginVal = (String) js().executeScript(
				"return jq('$drFormat .z-daterangebox-begin')[0].value;");
		assertNotNull(beginVal);
		assertTrue(beginVal.matches("\\d{4}-\\d{2}-\\d{2}"),
				"begin input value must follow yyyy-MM-dd, was '" + beginVal + "'");
	}

	// ===== D6 events =====

	/** D6: clicking the calendar icon fires onOpen on the server side. The
	 *  ZUL handler echoes the event by setting an adjacent label. */
	@Test
	public void testOnOpenEventFiresOnIconClick() {
		connect("/test2/F104-ZK-4305-attributes.zul");
		waitResponse();

		click(jq("$drEvents .z-daterangebox-button"));
		waitResponse();

		assertEquals("opened", jq("$lblOpen").text(),
				"onOpen handler must run when the user opens the popup");
	}

	/** D6: applying a range fires onChange with the chosen begin/end. The
	 *  ZUL handler writes "<beginMs>|<endMs>" into a label so the test can
	 *  assert against it. */
	@Test
	public void testOnChangeEventFiresWithBeginEndPayload() {
		connect("/test2/F104-ZK-4305-attributes.zul");
		waitResponse();

		click(jq("$drEvents .z-daterangebox-button"));
		waitResponse();
		clickCellInPanel("$drEvents", 0, 10);
		clickCellInPanel("$drEvents", 0, 15);
		waitForJs("/^\\d+\\|\\d+$/.test(jq('$lblChange').text())");
		waitResponse();

		String txt = jq("$lblChange").text();
		assertNotNull(txt);
		assertTrue(txt.matches("\\d+\\|\\d+"),
				"onChange payload must be 'beginMs|endMs', was '" + txt + "'");
		String[] parts = txt.split("\\|");
		long begin = Long.parseLong(parts[0]);
		long end = Long.parseLong(parts[1]);
		assertTrue(end > begin,
				"end timestamp must be after begin timestamp (" + begin + " < " + end + ")");
	}

	/** D6: clicking the same calendar day twice commits a valid same-day range. */
	@Test
	public void testSameDayDoubleClickCommitsRange() {
		connect("/test2/F104-ZK-4305-attributes.zul");
		waitResponse();

		click(jq("$drEvents .z-daterangebox-button"));
		waitResponse();
		clickCellInPanel("$drEvents", 0, 10);
		clickCellInPanel("$drEvents", 0, 10);
		waitForJs("/^\\d+\\|\\d+$/.test(jq('$lblChange').text())");
		waitResponse();

		String[] parts = jq("$lblChange").text().split("\\|");
		assertEquals(parts[0], parts[1],
				"same-day range must report identical begin/end timestamps");
	}

	// ===== H accessibility =====

	/** H1: root daterangebox div has role="group" and a meaningful aria-label. */
	@Test
	public void testRootHasAriaRoleGroup() {
		connect("/test2/F104-ZK-4305-attributes.zul");
		waitResponse();

		String role = (String) js().executeScript(
				"return jq('$drDisabled')[0].getAttribute('role');");
		String label = (String) js().executeScript(
				"return jq('$drDisabled')[0].getAttribute('aria-label');");
		assertEquals("group", role, "Root must have role=group");
		// The default 'date range' label moved to the EE za11y tier (msgza11y);
		// only the za11y augment emits it (the role above is the CE baseline).
		if (!za11yActive()) return;
		assertEquals("date range", label, "Root must carry za11y aria-label='date range'");
	}

	/** H1: app-supplied client attributes must not be shadowed by root defaults. */
	@Test
	public void testRootKeepsCustomAriaLabel() {
		connect("/test2/F104-ZK-4305-attributes.zul");
		waitResponse();

		String label = (String) js().executeScript(
				"return jq('$drCustomAria')[0].getAttribute('aria-label');");
		assertEquals("Choose a date range", label,
				"ca:aria-label must remain the root accessible name");
	}

	/** Float coordination: opening another ZK popup must close the daterange popup. */
	@Test
	public void testPopupClosesWhenAnotherFloatOpens() {
		connect("/test2/F104-ZK-4305-attributes.zul");
		waitResponse();

		click(jq("$drFormat .z-daterangebox-button"));
		waitResponse();
		assertTrue(jq(".z-daterangebox-popup:visible").exists(),
				"daterange popup must be visible before opening another float");

		click(jq("$dbFloat .z-datebox-button"));
		waitResponse();
		waitForPopupClosed();

		assertFalse(jq(".z-daterangebox-popup:visible").exists(),
				"opening datebox popup must close daterange popup");
	}

	/** H2: calendar button advertises a popup of role 'dialog'. */
	@Test
	public void testButtonHasAriaHaspopupDialog() {
		connect("/test2/F104-ZK-4305-attributes.zul");
		waitResponse();

		String haspopup = (String) js().executeScript(
				"return jq('$drFormat .z-daterangebox-button')[0].getAttribute('aria-haspopup');");
		String role = (String) js().executeScript(
				"return jq('$drFormat .z-daterangebox-button')[0].getAttribute('role');");
		String label = (String) js().executeScript(
				"return jq('$drFormat .z-daterangebox-button')[0].getAttribute('aria-label');");
		assertEquals("dialog", haspopup,
				"Calendar button must carry aria-haspopup=dialog");
		assertEquals("button", role,
				"Calendar button must carry role=button");
		// The default 'open calendar' label moved to the EE za11y tier (msgza11y);
		// only the za11y augment emits it.
		if (!za11yActive()) return;
		assertEquals("open calendar", label,
				"Calendar button must carry za11y aria-label='open calendar'");
	}

	// ===== Batch B/C: deeper interaction scenarios =====

	/** S13: pressing ESC closes the popup and discards staged selection.
	 *  Mirror of testCancelDiscardsStaging but via the ESC key. */
	@Test
	public void testEscClosesPopupAndDiscardsStaging() {
		connect("/test2/F104-ZK-4305-attributes.zul");
		waitResponse();

		click(jq("$drFormat .z-daterangebox-button"));
		waitResponse();
		// Pre-stage a date so we can verify it gets discarded.
		js().executeScript(
				"var box = zk.Widget.$(jq('$drFormat')[0]);"
				+ "var pane = box._rangePopup.$n().querySelector('.z-calendar');"
				+ "var cells = pane.querySelectorAll('td.z-calendar-cell');"
				+ "for (var i=0;i<cells.length;i++) {"
				+ "  if (jq(cells[i]).data('value') === 12 && (cells[i]._monofs||0) === 0) {"
				+ "    zk.Widget.$(pane)._clickDate({target: cells[i], domTarget: cells[i], stop: function(){}});"
				+ "    return;"
				+ "  }"
				+ "}");
		waitResponse();

		// Press ESC.
		js().executeScript(
				"var ev = new KeyboardEvent('keydown', { key: 'Escape', bubbles: true });"
				+ "document.dispatchEvent(ev);");
		waitForPopupClosed();
		waitResponse();

		String display = (String) js().executeScript(
				"var p = document.querySelector('.z-daterangebox-popup');"
				+ "return p ? p.style.display : null;");
		assertEquals("none", display, "ESC must hide the popup");

		String beginVal = (String) js().executeScript(
				"return jq('$drFormat .z-daterangebox-begin')[0].value;");
		assertTrue(beginVal == null || beginVal.isEmpty(),
				"ESC must discard staged selection (begin input stays empty)");
	}

	/** S8: range that crosses a year boundary still gets begin / mid / end
	 *  classes correctly applied. Drives Calendar.setValue to jump panel 0
	 *  to December and panel 1 follows naturally to January. */
	@Test
	public void testCrossYearRangeHighlight() {
		connect("/test2/F104-ZK-4305-attributes.zul");
		waitResponse();

		click(jq("$drFormat .z-daterangebox-button"));
		waitResponse();

		// Force panel 0 to Dec 2026 and panel 1 to Jan 2027 by setting
		// the calendar widget value directly.
		js().executeScript(
				"var box = zk.Widget.$(jq('$drFormat')[0]);"
				+ "var pop = box._rangePopup;"
				+ "pop._panels[0].setValue(Dates.newInstance(new Date(2026, 11, 1).getTime()));"
				+ "pop._panels[1].setValue(Dates.newInstance(new Date(2027, 0, 1).getTime()));");
		waitForJs("(function(){var p=zk.Widget.$(jq('$drFormat')[0])._rangePopup._panels;"
				+ "var v0=new Date(p[0].getValue().getTime());"
				+ "return v0.getFullYear()===2026 && v0.getMonth()===11;})()");
		waitResponse();

		// Click 30 in Dec (panel 0) then 3 in Jan (panel 1).
		js().executeScript(
				"function pick(p, day) {"
				+ "  var pane = p.$n();"
				+ "  var cells = pane.querySelectorAll('td.z-calendar-cell');"
				+ "  for (var i=0;i<cells.length;i++) {"
				+ "    if (jq(cells[i]).data('value') === day && (cells[i]._monofs||0) === 0) {"
				+ "      p._clickDate({target: cells[i], domTarget: cells[i], stop: function(){}});"
				+ "      return;"
				+ "    }"
				+ "  }"
				+ "}"
				+ "var box = zk.Widget.$(jq('$drFormat')[0]);"
				+ "pick(box._rangePopup._panels[0], 30);"
				+ "pick(box._rangePopup._panels[1], 3);");
		waitForJs("jq('.z-cell-range-begin').length>=1 && jq('.z-cell-range-end').length>=1");
		waitResponse();

		assertTrue(jq(".z-cell-range-begin").length() >= 1,
				"Cross-year range must have a begin highlight");
		assertTrue(jq(".z-cell-range-end").length() >= 1,
				"Cross-year range must have an end highlight");
		assertTrue(jq(".z-cell-range-mid").length() >= 1,
				"Cross-year range must have at least one mid cell");
	}

	/** S11: 2024 (leap year) shows 29 in February; 2025 (non-leap) shows
	 *  only up to 28. The Calendar's day grid is responsible for this — we
	 *  just confirm by jumping panel 0's value. */
	@Test
	public void testLeapYearFebruary29SelectableInLeapYear() {
		connect("/test2/F104-ZK-4305-attributes.zul");
		waitResponse();

		click(jq("$drFormat .z-daterangebox-button"));
		waitResponse();

		// Jump panel 0 to Feb 2024 (leap).
		js().executeScript(
				"var box = zk.Widget.$(jq('$drFormat')[0]);"
				+ "box._rangePopup._panels[0].setValue(Dates.newInstance(new Date(2024, 1, 1).getTime()));");
		waitForJs("(function(){var v=new Date(zk.Widget.$(jq('$drFormat')[0])"
				+ "._rangePopup._panels[0].getValue().getTime());"
				+ "return v.getFullYear()===2024 && v.getMonth()===1;})()");
		waitResponse();

		Long has29Leap = (Long) js().executeScript(
				"var box = zk.Widget.$(jq('$drFormat')[0]);"
				+ "var pane = box._rangePopup._panels[0].$n();"
				+ "var cells = pane.querySelectorAll('td.z-calendar-cell');"
				+ "var n = 0;"
				+ "for (var i=0;i<cells.length;i++) {"
				+ "  if (jq(cells[i]).data('value') === 29 && (cells[i]._monofs||0) === 0) n++;"
				+ "}"
				+ "return n;");
		assertEquals(1L, has29Leap.longValue(),
				"Feb 2024 (leap year) must have a clickable day-29 cell");

		// Now jump panel 0 to Feb 2025 (non-leap).
		js().executeScript(
				"var box = zk.Widget.$(jq('$drFormat')[0]);"
				+ "box._rangePopup._panels[0].setValue(Dates.newInstance(new Date(2025, 1, 1).getTime()));");
		waitForJs("(function(){var v=new Date(zk.Widget.$(jq('$drFormat')[0])"
				+ "._rangePopup._panels[0].getValue().getTime());"
				+ "return v.getFullYear()===2025 && v.getMonth()===1;})()");
		waitResponse();

		Long has29NonLeap = (Long) js().executeScript(
				"var box = zk.Widget.$(jq('$drFormat')[0]);"
				+ "var pane = box._rangePopup._panels[0].$n();"
				+ "var cells = pane.querySelectorAll('td.z-calendar-cell');"
				+ "var n = 0;"
				+ "for (var i=0;i<cells.length;i++) {"
				+ "  if (jq(cells[i]).data('value') === 29 && (cells[i]._monofs||0) === 0) n++;"
				+ "}"
				+ "return n;");
		assertEquals(0L, has29NonLeap.longValue(),
				"Feb 2025 (non-leap) must NOT have a day-29 cell of the current month");
	}

	/** S16: a date forbidden by `constraint` ("no past") gets the
	 *  z-calendar-disabled class so the user cannot pick it. */
	@Test
	public void testConstraintDisablesPastDates() {
		connect("/test2/F104-ZK-4305-attributes.zul");
		waitResponse();

		click(jq("$drConstraint .z-daterangebox-button"));
		waitResponse();

		// Jump panel 0 to a known past month so we can be sure SOME cells
		// are in the past relative to "today".
		js().executeScript(
				"var box = zk.Widget.$(jq('$drConstraint')[0]);"
				+ "box._rangePopup._panels[0].setValue(Dates.newInstance(new Date(2020, 0, 15).getTime()));");
		waitForJs("(function(){var v=new Date(zk.Widget.$(jq('$drConstraint')[0])"
				+ "._rangePopup._panels[0].getValue().getTime());"
				+ "return v.getFullYear()===2020 && v.getMonth()===0;})()");
		waitResponse();

		Long disabledCount = (Long) js().executeScript(
				"var box = zk.Widget.$(jq('$drConstraint')[0]);"
				+ "var pane = box._rangePopup._panels[0].$n();"
				+ "return pane.querySelectorAll('td.z-calendar-cell.z-calendar-disabled').length;");
		assertTrue(disabledCount > 0,
				"At least some past-month cells must be disabled by 'no past' constraint");
	}

	/** S19: bound DateRange value populates the box, and reopening the
	 *  popup anchors panel 0 to the begin's month so highlight is visible
	 *  immediately. */
	@Test
	public void testBoundDateRangeValueAnchorsPopupOnReopen() {
		connect("/test2/F104-ZK-4305-binding.zul");
		waitResponse();

		// Drive a range pick then apply on the @bind(vm.range) box.
		click(jq("$drValue .z-daterangebox-button"));
		waitResponse();
		js().executeScript(
				"function pick(p, day) {"
				+ "  var pane = p.$n();"
				+ "  var cells = pane.querySelectorAll('td.z-calendar-cell');"
				+ "  for (var i=0;i<cells.length;i++) {"
				+ "    if (jq(cells[i]).data('value') === day && (cells[i]._monofs||0) === 0) {"
				+ "      p._clickDate({target: cells[i], domTarget: cells[i], stop: function(){}});"
				+ "      return;"
				+ "    }"
				+ "  }"
				+ "}"
				+ "var box = zk.Widget.$(jq('$drValue')[0]);"
				+ "pick(box._rangePopup._panels[0], 8);"
				+ "pick(box._rangePopup._panels[0], 17);");
		waitForBeginInputPopulated("$drValue");
		waitResponse();

		String beginVal = (String) js().executeScript(
				"return jq('$drValue .z-daterangebox-begin')[0].value;");
		assertFalse(beginVal == null || beginVal.isEmpty(),
				"Bound @bind(vm.range) must round-trip the chosen begin date back to the input");

		// Reopen and verify highlight is on a freshly-anchored panel.
		click(jq("$drValue .z-daterangebox-button"));
		waitResponse();

		assertTrue(jq("$drValue").exists());
		assertTrue(jq(".z-cell-range-begin").length() >= 1,
				"Reopening a bound box must restore range-begin highlight");
		assertTrue(jq(".z-cell-range-end").length() >= 1,
				"Reopening a bound box must restore range-end highlight");
	}

	/** S18: setValue programmatically on the server pushes back to the
	 *  client and is honoured equally with a user pick. We validate by
	 *  driving a range via the same code path as a user click and asserting
	 *  the box's begin/end inputs reflect the change. */
	@Test
	public void testProgrammaticPickPathProducesSameInputState() {
		connect("/test2/F104-ZK-4305-attributes.zul");
		waitResponse();

		click(jq("$drFormat .z-daterangebox-button"));
		waitResponse();
		// Programmatic path: stage the range directly on the popup and call
		// _doApply, exercising the same commit pipeline a user click uses.
		js().executeScript(
				"var box = zk.Widget.$(jq('$drFormat')[0]);"
				+ "var pop = box._rangePopup;"
				+ "var t = new Date(); t.setHours(0,0,0,0);"
				+ "pop._stagedBegin = t;"
				+ "var t2 = new Date(t.getTime() + 3*86400000);"
				+ "pop._stagedEnd = t2;"
				+ "pop._applyHighlight();"
				+ "pop._doApply();");
		waitForBeginInputPopulated("$drFormat");
		waitResponse();

		String beginVal = (String) js().executeScript(
				"return jq('$drFormat .z-daterangebox-begin')[0].value;");
		String endVal = (String) js().executeScript(
				"return jq('$drFormat .z-daterangebox-end')[0].value;");
		assertFalse(beginVal == null || beginVal.isEmpty(),
				"Programmatic pick path must populate begin input");
		assertFalse(endVal == null || endVal.isEmpty(),
				"Programmatic pick path must populate end input");
		assertTrue(beginVal.matches("\\d{4}-\\d{2}-\\d{2}"),
				"begin input value must respect format=yyyy-MM-dd, was '" + beginVal + "'");
	}

	// ===== showTime =====

	/** showTime=true must render two Timebox widgets (begin time / end
	 *  time) under the calendar panels. */
	@Test
	public void testShowTimeRendersTimeboxes() {
		connect("/test2/F104-ZK-4305-attributes.zul");
		waitResponse();

		click(jq("$drShowTime .z-daterangebox-button"));
		waitResponse();

		Long timeboxCount = (Long) js().executeScript(
				"var box = zk.Widget.$(jq('$drShowTime')[0]);"
				+ "var pop = box._rangePopup;"
				+ "if (!pop) return 0;"
				+ "var times = pop.$n('times');"
				+ "if (!times) return 0;"
				+ "return times.querySelectorAll('.z-timebox').length;");
		assertEquals(2L, timeboxCount.longValue(),
				"showTime=true must render 2 Timeboxes under the panels");
	}

	/** showTime=true: applying a range must produce inputs whose value
	 *  carries the time component from the Timeboxes (not just the date). */
	@Test
	public void testShowTimeMergesTimeIntoCommittedValue() {
		connect("/test2/F104-ZK-4305-attributes.zul");
		waitResponse();

		click(jq("$drShowTime .z-daterangebox-button"));
		waitResponse();

		// Pick begin = day 10, end = day 15 in panel 0.
		js().executeScript(
				"function pick(p, day) {"
				+ "  var pane = p.$n();"
				+ "  var cells = pane.querySelectorAll('td.z-calendar-cell');"
				+ "  for (var i=0;i<cells.length;i++) {"
				+ "    if (jq(cells[i]).data('value') === day && (cells[i]._monofs||0) === 0) {"
				+ "      p._clickDate({target: cells[i], domTarget: cells[i], stop: function(){}});"
				+ "      return;"
				+ "    }"
				+ "  }"
				+ "}"
				+ "var box = zk.Widget.$(jq('$drShowTime')[0]);"
				+ "pick(box._rangePopup._panels[0], 10);"
				+ "pick(box._rangePopup._panels[0], 15);");
		waitResponse();

		waitForBeginInputPopulated("$drShowTime");
		waitResponse();

		// Default begin time = 00:00, end time = 23:59 — verify both end
		// up in the input strings (format = yyyy-MM-dd HH:mm).
		String beginVal = (String) js().executeScript(
				"return jq('$drShowTime .z-daterangebox-begin')[0].value;");
		String endVal = (String) js().executeScript(
				"return jq('$drShowTime .z-daterangebox-end')[0].value;");
		assertTrue(beginVal != null && beginVal.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}"),
				"showTime begin value must follow yyyy-MM-dd HH:mm, was '" + beginVal + "'");
		assertTrue(endVal != null && endVal.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}"),
				"showTime end value must follow yyyy-MM-dd HH:mm, was '" + endVal + "'");
		assertTrue(beginVal.endsWith("00:00"),
				"showTime begin default time must be 00:00, was '" + beginVal + "'");
		assertTrue(endVal.endsWith("23:59"),
				"showTime end default time must be 23:59, was '" + endVal + "'");
	}

	/** showTime + foreign timeZone: the time-row Timebox must display the SAME
	 *  wall-clock time as the begin/end input. The input renders in the box
	 *  timeZone (Asia/Tokyo) via _formatDate's `di.tz()`; before the fix the
	 *  Timebox was seeded with the browser-local time-of-day of the same
	 *  instant (_syncTimeFromDate read native Date.getHours()), so the two
	 *  diverged by the zone offset and a Timebox edit committed a wrong time.
	 *  Teeth: distinguishes on any run whose browser zone != Asia/Tokyo
	 *  (UTC+9); a browser==Tokyo run passes correctly (non-teeth). */
	@Test
	public void testShowTimeTimeboxMatchesInputUnderForeignTimeZone() {
		connect("/test2/F104-ZK-4305-attributes.zul");
		waitResponse();

		// Seed begin/end at fixed instants whose Asia/Tokyo wall time is a
		// distinctive HH:mm: 2026-06-10 00:30 UTC == 09:30 Tokyo,
		// 2026-06-12 06:00 UTC == 15:00 Tokyo.
		js().executeScript(
				"var box = zk.Widget.$(jq('$drShowTimeTz')[0]);"
				+ "box._beginValue = new Date(Date.UTC(2026,5,10,0,30,0));"
				+ "box._endValue = new Date(Date.UTC(2026,5,12,6,0,0));"
				+ "box._renderInput('begin'); box._renderInput('end');");
		waitResponse();

		click(jq("$drShowTimeTz .z-daterangebox-button"));
		waitResponse();

		// The input renders the box-tz wall time; assert it really is Tokyo
		// time so the comparison below isn't two browser-local values.
		String beginInputTime = (String) js().executeScript(
				"return jq('$drShowTimeTz .z-daterangebox-begin')[0].value.split(' ')[1];");
		assertEquals("09:30", beginInputTime,
				"begin input must render 09:30 Asia/Tokyo for 00:30 UTC, was " + beginInputTime);

		// The begin Timebox's displayed wall time must equal the input's.
		String beginTbTime = (String) js().executeScript(
				"var v = zk.Widget.$(jq('$drShowTimeTz')[0])._rangePopup._beginTime.getValue();"
				+ "return ('0'+v.getHours()).slice(-2)+':'+('0'+v.getMinutes()).slice(-2);");
		assertEquals(beginInputTime, beginTbTime,
				"begin Timebox wall-time must equal begin input wall-time under timeZone (input="
				+ beginInputTime + ", timebox=" + beginTbTime + ")");

		String endInputTime = (String) js().executeScript(
				"return jq('$drShowTimeTz .z-daterangebox-end')[0].value.split(' ')[1];");
		String endTbTime = (String) js().executeScript(
				"var v = zk.Widget.$(jq('$drShowTimeTz')[0])._rangePopup._endTime.getValue();"
				+ "return ('0'+v.getHours()).slice(-2)+':'+('0'+v.getMinutes()).slice(-2);");
		assertEquals(endInputTime, endTbTime,
				"end Timebox wall-time must equal end input wall-time under timeZone (input="
				+ endInputTime + ", timebox=" + endTbTime + ")");
	}

	/** showTime defaults to false when format has no time tokens — the
	 *  default daterangebox (`drFormat`, format=yyyy-MM-dd) must NOT
	 *  render Timebox widgets in its popup. */
	@Test
	public void testShowTimeOffWhenFormatHasNoTime() {
		connect("/test2/F104-ZK-4305-attributes.zul");
		waitResponse();

		click(jq("$drFormat .z-daterangebox-button"));
		waitResponse();

		Long timeboxCount = (Long) js().executeScript(
				"var box = zk.Widget.$(jq('$drFormat')[0]);"
				+ "var pop = box._rangePopup;"
				+ "if (!pop) return 0;"
				+ "var times = pop.$n('times');"
				+ "if (!times) return 0;"
				+ "return times.querySelectorAll('.z-timebox').length;");
		assertEquals(0L, timeboxCount.longValue(),
				"format without H/m/s must not render Timeboxes");
	}

	/** showTime: programmatically change the begin Timebox to 09:30 and the
	 *  end Timebox to 18:00 before apply — the committed value must reflect
	 *  the user-chosen times, not the 00:00 / 23:59 defaults. */
	@Test
	public void testShowTimeMergeUsesUserChangedTimes() {
		connect("/test2/F104-ZK-4305-attributes.zul");
		waitResponse();

		click(jq("$drShowTime .z-daterangebox-button"));
		waitResponse();

		// Stage Timebox values BEFORE picking the second cell, so the
		// auto-apply (fired 200ms after the second click) picks them up.
		js().executeScript(
				"var box = zk.Widget.$(jq('$drShowTime')[0]);"
				+ "var pop = box._rangePopup;"
				+ "var t1 = new Date(); t1.setHours(9, 30, 0, 0);"
				+ "pop._beginTime.setValue(Dates.newInstance(t1.getTime()));"
				+ "var t2 = new Date(); t2.setHours(18, 0, 0, 0);"
				+ "pop._endTime.setValue(Dates.newInstance(t2.getTime()));");
		waitResponse();

		// Pick begin then end. Auto-apply fires 200ms after the second
		// click, merging the (already-set) Timebox values into the
		// committed range.
		js().executeScript(
				"function pick(p, day) {"
				+ "  var pane = p.$n();"
				+ "  var cells = pane.querySelectorAll('td.z-calendar-cell');"
				+ "  for (var i=0;i<cells.length;i++) {"
				+ "    if (jq(cells[i]).data('value') === day && (cells[i]._monofs||0) === 0) {"
				+ "      p._clickDate({target: cells[i], domTarget: cells[i], stop: function(){}});"
				+ "      return;"
				+ "    }"
				+ "  }"
				+ "}"
				+ "var box = zk.Widget.$(jq('$drShowTime')[0]);"
				+ "pick(box._rangePopup._panels[0], 10);"
				+ "pick(box._rangePopup._panels[0], 15);");
		waitForJs("(function(){var v=jq('$drShowTime .z-daterangebox-begin')[0].value;"
				+ "return v && v.endsWith('09:30');})()");
		waitResponse();

		String beginVal = (String) js().executeScript(
				"return jq('$drShowTime .z-daterangebox-begin')[0].value;");
		String endVal = (String) js().executeScript(
				"return jq('$drShowTime .z-daterangebox-end')[0].value;");
		assertTrue(beginVal != null && beginVal.endsWith("09:30"),
				"begin must reflect user-chosen 09:30, was '" + beginVal + "'");
		assertTrue(endVal != null && endVal.endsWith("18:00"),
				"end must reflect user-chosen 18:00, was '" + endVal + "'");
	}

	/** showTime + reopen: after applying a range with non-default times,
	 *  reopening the popup must populate the Timeboxes with those times
	 *  rather than reverting to 00:00 / 23:59 defaults. */
	@Test
	public void testShowTimeReopenSyncsTimeFromCommittedValue() {
		connect("/test2/F104-ZK-4305-attributes.zul");
		waitResponse();

		click(jq("$drShowTime .z-daterangebox-button"));
		waitResponse();
		js().executeScript(
				"function pick(p, day) {"
				+ "  var pane = p.$n();"
				+ "  var cells = pane.querySelectorAll('td.z-calendar-cell');"
				+ "  for (var i=0;i<cells.length;i++) {"
				+ "    if (jq(cells[i]).data('value') === day && (cells[i]._monofs||0) === 0) {"
				+ "      p._clickDate({target: cells[i], domTarget: cells[i], stop: function(){}});"
				+ "      return;"
				+ "    }"
				+ "  }"
				+ "}"
				+ "var box = zk.Widget.$(jq('$drShowTime')[0]);"
				+ "pick(box._rangePopup._panels[0], 10);"
				+ "pick(box._rangePopup._panels[0], 15);"
				+ "var t1 = new Date(); t1.setHours(8, 15, 0, 0);"
				+ "box._rangePopup._beginTime.setValue(Dates.newInstance(t1.getTime()));"
				+ "var t2 = new Date(); t2.setHours(20, 45, 0, 0);"
				+ "box._rangePopup._endTime.setValue(Dates.newInstance(t2.getTime()));");
		waitResponse();
		waitForJs("(function(){var v=jq('$drShowTime .z-daterangebox-begin')[0].value;"
				+ "return v && v.endsWith('08:15');})()");
		waitResponse();

		// Reopen.
		click(jq("$drShowTime .z-daterangebox-button"));
		waitResponse();

		String beginTime = (String) js().executeScript(
				"var v = zk.Widget.$(jq('$drShowTime')[0])._rangePopup._beginTime.getValue();"
				+ "var d = new Date(v.getTime());"
				+ "return ('0'+d.getHours()).slice(-2) + ':' + ('0'+d.getMinutes()).slice(-2);");
		String endTime = (String) js().executeScript(
				"var v = zk.Widget.$(jq('$drShowTime')[0])._rangePopup._endTime.getValue();"
				+ "var d = new Date(v.getTime());"
				+ "return ('0'+d.getHours()).slice(-2) + ':' + ('0'+d.getMinutes()).slice(-2);");
		assertEquals("08:15", beginTime,
				"reopened popup must show committed begin time 08:15");
		assertEquals("20:45", endTime,
				"reopened popup must show committed end time 20:45");
	}

	// ===== Linked panels =====

	/** Bug guard for the "two panels show same month" scenario the user
	 *  reported: panels are always linked, so advancing panel 0 forward
	 *  by 1 month must also advance panel 1 by 1 month so they remain
	 *  consecutive. */
	@Test
	public void testLinkedPanelsAdvanceTogether() {
		connect("/test2/F104-ZK-4305-attributes.zul");
		waitResponse();

		click(jq("$drFormat .z-daterangebox-button"));
		waitResponse();

		// Capture baseline: panel 0 month vs panel 1 month, must differ by 1.
		Long baselineDelta = (Long) js().executeScript(
				"var box = zk.Widget.$(jq('$drFormat')[0]);"
				+ "var p = box._rangePopup._panels;"
				+ "var v0 = new Date(p[0].getValue().getTime());"
				+ "var v1 = new Date(p[1].getValue().getTime());"
				+ "return (v1.getFullYear() - v0.getFullYear()) * 12 + (v1.getMonth() - v0.getMonth());");
		assertEquals(1L, baselineDelta.longValue(),
				"baseline: linked panels must be 1 month apart on open");

		// Click panel 0's right arrow to advance it 1 month. Stash the
		// pre-click panel-1 month so we can wait until linkage has caught up.
		Long panel1MonthBefore = (Long) js().executeScript(
				"var box = zk.Widget.$(jq('$drFormat')[0]);"
				+ "var v1 = new Date(box._rangePopup._panels[1].getValue().getTime());"
				+ "return v1.getFullYear()*12 + v1.getMonth();");
		js().executeScript(
				"var box = zk.Widget.$(jq('$drFormat')[0]);"
				+ "var pane = box._rangePopup._panels[0];"
				+ "var arrow = pane.$n().querySelector('.z-calendar-right');"
				+ "pane._clickArrow({domTarget: arrow, stop: function(){}});");
		final long expectedPanel1Month = panel1MonthBefore.longValue() + 1L;
		waitForJs("(function(){var v=new Date(zk.Widget.$(jq('$drFormat')[0])"
				+ "._rangePopup._panels[1].getValue().getTime());"
				+ "return v.getFullYear()*12+v.getMonth()===" + expectedPanel1Month + ";})()");
		waitResponse();

		Long afterDelta = (Long) js().executeScript(
				"var box = zk.Widget.$(jq('$drFormat')[0]);"
				+ "var p = box._rangePopup._panels;"
				+ "var v0 = new Date(p[0].getValue().getTime());"
				+ "var v1 = new Date(p[1].getValue().getTime());"
				+ "return (v1.getFullYear() - v0.getFullYear()) * 12 + (v1.getMonth() - v0.getMonth());");
		assertEquals(1L, afterDelta.longValue(),
				"after panel 0 forward: panels must STILL be 1 month apart "
				+ "(panel 1 must auto-advance to keep linkage)");
	}

	/** Linked popup must apply the {@code z-daterangebox-popup-linked}
	 *  class so the inner-edge arrow CSS rules kick in. */
	@Test
	public void testLinkedPopupCarriesLinkedClass() {
		connect("/test2/F104-ZK-4305-attributes.zul");
		waitResponse();

		click(jq("$drFormat .z-daterangebox-button"));
		waitResponse();

		Boolean hasLinkedClass = (Boolean) js().executeScript(
				"var box = zk.Widget.$(jq('$drFormat')[0]);"
				+ "var p = box._rangePopup.$n();"
				+ "return p.classList.contains('z-daterangebox-popup-linked');");
		assertTrue(hasLinkedClass,
				"linked popup must carry z-daterangebox-popup-linked class");
	}

	/** showTime runtime toggle: turning showTime on after the popup is
	 *  already built must reveal the Timebox row without page reload. */
	@Test
	public void testShowTimeRuntimeToggle() {
		connect("/test2/F104-ZK-4305-attributes.zul");
		waitResponse();

		// drFormat starts with showTime=false. Open popup, verify no Timeboxes,
		// then flip showTime=true and re-check.
		click(jq("$drFormat .z-daterangebox-button"));
		waitResponse();

		Long beforeCount = (Long) js().executeScript(
				"var box = zk.Widget.$(jq('$drFormat')[0]);"
				+ "return box._rangePopup.$n('times').querySelectorAll('.z-timebox').length;");
		assertEquals(0L, beforeCount.longValue(),
				"baseline: format-only daterangebox must not have Timeboxes");

		js().executeScript(
				"zk.Widget.$(jq('$drFormat')[0]).setShowTime(true);");
		waitForJs("zk.Widget.$(jq('$drFormat')[0])._rangePopup.$n('times')"
				+ ".querySelectorAll('.z-timebox').length===2");
		waitResponse();

		Long afterCount = (Long) js().executeScript(
				"var box = zk.Widget.$(jq('$drFormat')[0]);"
				+ "return box._rangePopup.$n('times').querySelectorAll('.z-timebox').length;");
		assertEquals(2L, afterCount.longValue(),
				"after setShowTime(true), Timeboxes must appear without reopen");
	}

	// ===== showTime + minNights =====

	/** showTime + minNights=1: an overnight range whose wall-clock duration
	 *  is &lt; 24h but spans 1 calendar night must be accepted. Pre-fix,
	 *  the server measured nights via Duration.toDays() which truncates
	 *  10h to 0 nights and rejected this valid booking. The fix measures
	 *  calendar days between LocalDate(begin) and LocalDate(end) instead. */
	@Test
	public void testShowTimeMinNightsAcceptsOvernight() {
		connect("/test2/F104-ZK-4305-attributes.zul");
		waitResponse();

		click(jq("$drShowTimeMinNights .z-daterangebox-button"));
		waitResponse();

		// Pick day-10 (begin) and day-11 (end) on panel 0, then set begin
		// time to 23:00 and end time to 09:00 so wall-clock duration is 10h
		// but the calendar-night count is 1.
		js().executeScript(
				"var box = zk.Widget.$(jq('$drShowTimeMinNights')[0]);"
				+ "var popup = box._rangePopup;"
				+ "var panel = popup._panels[0];"
				+ "function pick(day) {"
				+ "  var cells = panel.$n().querySelectorAll('td.z-calendar-cell');"
				+ "  for (var i=0;i<cells.length;i++) {"
				+ "    if (jq(cells[i]).data('value') === day && (cells[i]._monofs||0) === 0) {"
				+ "      panel._clickDate({target: cells[i], domTarget: cells[i], stop: function(){}});"
				+ "      return;"
				+ "    }"
				+ "  }"
				+ "}"
				+ "pick(10); pick(11);"
				+ "var beginT = new Date(); beginT.setHours(23,0,0,0);"
				+ "var endT = new Date(); endT.setHours(9,0,0,0);"
				+ "popup._beginTime.setValue(beginT);"
				+ "popup._endTime.setValue(endT);"
				+ "popup._doApply();");
		waitResponse();

		String beginVal = (String) js().executeScript(
				"return jq('$drShowTimeMinNights .z-daterangebox-begin')[0].value;");
		String endVal = (String) js().executeScript(
				"return jq('$drShowTimeMinNights .z-daterangebox-end')[0].value;");
		assertTrue(beginVal != null && beginVal.endsWith("23:00"),
				"overnight begin must be committed at 23:00, was '" + beginVal + "'");
		assertTrue(endVal != null && endVal.endsWith("09:00"),
				"overnight end must be committed at 09:00, was '" + endVal + "'");
	}

	// ===== maxNights =====

	/** maxNights=3: a 9-night range (Jan 1 -> Jan 10) exceeds the cap and
	 *  must be rejected server-side via validateCandidateOrThrow. The
	 *  snap-back pushes the box's pre-call state (null for a fresh box), so
	 *  the over-long pair must NOT appear in _beginValue / _endValue. Mirrors
	 *  {@link #testApplyRangeReversedRejectedByServer}'s rejection contract. */
	@Test
	public void testMaxNightsRejectsTooLongRange() {
		connect("/test2/F104-ZK-4305-attributes.zul");
		waitResponse();

		final long rejectedBeginMs = (Long) js().executeScript(
				"return new Date(2026, 0, 1).getTime();");

		js().executeScript(
				"var w = zk.Widget.$('$drMaxNights');"
				+ "w.applyRange(new Date(2026, 0, 1), new Date(2026, 0, 10), true);");
		waitResponse();

		Long beginMs = (Long) js().executeScript(
				"var v = zk.Widget.$('$drMaxNights')._beginValue;"
				+ "return v ? v.getTime() : null;");
		Long endMs = (Long) js().executeScript(
				"var v = zk.Widget.$('$drMaxNights')._endValue;"
				+ "return v ? v.getTime() : null;");
		// The 9-night pair must not have committed (cap is 3).
		if (beginMs != null) {
			assertNotEquals((Long) rejectedBeginMs, beginMs,
					"Range exceeding maxNights must not commit to _beginValue");
		}
		if (beginMs != null && endMs != null) {
			long nights = (endMs - beginMs) / 86_400_000L;
			assertTrue(nights <= 3,
					"After rejection, snap-back must leave a range within "
					+ "maxNights=3; got " + nights + " nights");
		}
	}

	/** maxNights=3: a range exactly at the cap (Jan 1 -> Jan 4 = 3 nights)
	 *  must be accepted and committed. */
	@Test
	public void testMaxNightsAcceptsRangeAtLimit() {
		connect("/test2/F104-ZK-4305-attributes.zul");
		waitResponse();

		js().executeScript(
				"var w = zk.Widget.$('$drMaxNights');"
				+ "w.applyRange(new Date(2026, 0, 1), new Date(2026, 0, 4), true);");
		waitResponse();

		Long beginMs = (Long) js().executeScript(
				"var v = zk.Widget.$('$drMaxNights')._beginValue;"
				+ "return v ? v.getTime() : null;");
		Long endMs = (Long) js().executeScript(
				"var v = zk.Widget.$('$drMaxNights')._endValue;"
				+ "return v ? v.getTime() : null;");
		assertEquals((Long) js().executeScript("return new Date(2026, 0, 1).getTime();"),
				beginMs, "3-night range (== maxNights) must commit begin");
		assertEquals((Long) js().executeScript("return new Date(2026, 0, 4).getTime();"),
				endMs, "3-night range (== maxNights) must commit end");
	}

	// ===== Open-state sync (regression for missed-close drift) =====

	/** Regression: a client-initiated close (outside-click here) must echo
	 *  onOpen{open:false} to the server so the next server-side
	 *  setOpen(true) is not dedupe-skipped by the {@code _open != effective}
	 *  guard in {@code Daterangebox#setOpen}. Before the round-1 fix the
	 *  server never learned about the close, so the popup got stuck closed
	 *  on any subsequent setOpen(true). */
	@Test
	public void testOpenStateSyncRoundTrip() {
		connect("/test2/F104-ZK-4305-attributes.zul");
		waitResponse();

		// Open via the calendar button.
		click(jq("$drOpenSync .z-daterangebox-button"));
		waitResponse();
		assertEquals("opened", jq("$lblOpenSync").text(),
				"opening the popup must fire onOpen{true} server-side");

		// Close via outside-click (focusin into another input).
		js().executeScript(
				"jq('$drDisabled .z-daterangebox-begin')[0].focus();"
				+ "jq('$drDisabled .z-daterangebox-begin')[0].dispatchEvent("
				+ "  new FocusEvent('focusin', {bubbles: true}));");
		// Wait for the close echo to complete the AU round-trip rather than
		// a fixed sleep — robust against slow CI agents.
		new WebDriverWait(driver, Duration.ofSeconds(2))
				.until(d -> "closed".equals(jq("$lblOpenSync").text()));
		assertEquals("closed", jq("$lblOpenSync").text(),
				"client-initiated close must echo onOpen{false} so server _open resets");

		// Click the server-side reopen button. Without the close echo,
		// server thinks _open is still true and the AuInvoke would be
		// dedupe-skipped — popup would stay closed.
		click(jq("$btnReopen"));
		waitResponse();
		// onOpen{true} fires again on the reopen.
		assertEquals("opened", jq("$lblOpenSync").text(),
				"setOpen(true) after a missed close must successfully reopen");
		Boolean popupVisible = (Boolean) js().executeScript(
				"var box = zk.Widget.$(jq('$drOpenSync')[0]);"
				+ "var n = box._rangePopup && box._rangePopup.$n();"
				+ "return !!n && n.style.display !== 'none';");
		assertTrue(popupVisible != null && popupVisible,
				"popup DOM must be visible again after the server-side reopen");
	}

	// ===== Dialog focus management (WAI-ARIA dialog pattern) =====

	/** Opening the popup must move keyboard focus inside it — otherwise a
	 *  keyboard user has to Tab past the rest of the page before reaching
	 *  the calendar. */
	@Test
	public void testPopupOpenMovesFocusInside() {
		connect("/test2/F104-ZK-4305-attributes.zul");
		waitResponse();

		click(jq("$drEvents .z-daterangebox-button"));
		waitResponse();

		// open() schedules the focus via setTimeout(0) so the popup is
		// already visible before focus(). Poll for the post-setTimeout state.
		new WebDriverWait(driver, Duration.ofSeconds(2)).until(d -> {
			Boolean inside = (Boolean) js().executeScript(
					"var box = zk.Widget.$(jq('$drEvents')[0]);"
					+ "var pop = box._rangePopup;"
					+ "if (!pop || !pop.$n()) return false;"
					+ "return pop.$n().contains(document.activeElement);");
			return Boolean.TRUE.equals(inside);
		});
	}

	/** Closing the popup must return focus to whatever held it before the
	 *  popup opened. We open via the calendar button (so the button is the
	 *  prior-focus element), close via ESC, and assert the button regains
	 *  focus. */
	@Test
	public void testPopupCloseRestoresFocus() {
		connect("/test2/F104-ZK-4305-attributes.zul");
		waitResponse();

		// Click the calendar button — it becomes document.activeElement and
		// triggers _openPopup, which captures it as _returnFocusTo.
		click(jq("$drEvents .z-daterangebox-button"));
		waitResponse();
		// Wait until focus has actually moved into the popup (so close()'s
		// restore path has an observable "before" state).
		new WebDriverWait(driver, Duration.ofSeconds(2)).until(d -> {
			Boolean inside = (Boolean) js().executeScript(
					"var box = zk.Widget.$(jq('$drEvents')[0]);"
					+ "return box._rangePopup.$n().contains(document.activeElement);");
			return Boolean.TRUE.equals(inside);
		});

		// Close via ESC (capture-phase keydown handler).
		js().executeScript(
				"document.dispatchEvent(new KeyboardEvent('keydown',"
				+ " {key: 'Escape', bubbles: true}));");

		new WebDriverWait(driver, Duration.ofSeconds(2)).until(d -> {
			Boolean onBtn = (Boolean) js().executeScript(
					"return document.activeElement"
					+ " === jq('$drEvents .z-daterangebox-button')[0];");
			return Boolean.TRUE.equals(onBtn);
		});
	}

	// ===== Focus / blur close (Tab navigation) =====

	/** Tab/blur: when keyboard focus moves to an element OUTSIDE both the
	 *  popup and the box (e.g. user Tabs to next form field), the popup
	 *  must close. Mirrors ZK ComboWidget's onFloatUp pattern but
	 *  implemented via plain document focusin listener. */
	@Test
	public void testFocusinOutsideClosesPopup() {
		connect("/test2/F104-ZK-4305-attributes.zul");
		waitResponse();

		click(jq("$drFormat .z-daterangebox-button"));
		waitResponse();

		// Move focus to an element well outside the popup and the box —
		// the disabled daterangebox's begin input is on the same page.
		js().executeScript(
				"jq('$drDisabled .z-daterangebox-begin')[0].focus();"
				+ "jq('$drDisabled .z-daterangebox-begin')[0].dispatchEvent("
				+ "  new FocusEvent('focusin', {bubbles: true}));");
		waitForPopupClosed();
		waitResponse();

		String display = (String) js().executeScript(
				"var p = document.querySelector('.z-daterangebox-popup');"
				+ "return p ? p.style.display : null;");
		assertEquals("none", display,
				"focusin on a node outside popup+box must close the popup");
	}

	/** Focus moving INSIDE the popup (e.g. between Calendar arrow and
	 *  Timebox input) must NOT close the popup. */
	@Test
	public void testFocusinInsidePopupKeepsItOpen() {
		connect("/test2/F104-ZK-4305-attributes.zul");
		waitResponse();

		click(jq("$drFormat .z-daterangebox-button"));
		waitResponse();

		// Dispatch a focusin from a cell INSIDE the popup. The focusin
		// handler is synchronous (it inspects e.target and routes to
		// _closePopup only when outside) — proving the negative ("did
		// NOT close") means: any close echo would have generated an AU
		// round-trip, so waitResponse() either drains that round-trip
		// (and the assertion below correctly fails) or returns quickly
		// (handler ran synchronously and chose not to close). Either way
		// we get a deterministic answer without a fixed sleep.
		js().executeScript(
				"var box = zk.Widget.$(jq('$drFormat')[0]);"
				+ "var pane = box._rangePopup._panels[0].$n();"
				+ "pane.dispatchEvent(new FocusEvent('focusin', {bubbles: true}));");
		waitResponse();

		String display = (String) js().executeScript(
				"return document.querySelector('.z-daterangebox-popup').style.display;");
		assertFalse("none".equals(display),
				"focusin inside the popup must NOT close it");
	}

	// ===== Paste into input =====

	/** Right-click paste: dispatching a `paste` event with a valid date
	 *  string into the begin input must populate the value (the underlying
	 *  Calendar parser kicks in via the `change` event after paste). */
	@Test
	public void testPasteIntoBeginInputPopulatesValue() {
		connect("/test2/F104-ZK-4305-attributes.zul");
		waitResponse();

		// Drive paste via clipboard data + change event to mirror what the
		// browser fires after a real right-click paste.
		js().executeScript(
				"var inp = jq('$drFormat .z-daterangebox-begin')[0];"
				+ "inp.focus();"
				+ "inp.value = '2026-05-12';"
				+ "var dt = new DataTransfer();"
				+ "dt.setData('text/plain', '2026-05-12');"
				+ "inp.dispatchEvent(new ClipboardEvent('paste', "
				+ "  {clipboardData: dt, bubbles: true}));"
				+ "inp.dispatchEvent(new Event('change', {bubbles: true}));");
		waitResponse();

		String beginVal = (String) js().executeScript(
				"return jq('$drFormat .z-daterangebox-begin')[0].value;");
		assertEquals("2026-05-12", beginVal,
				"pasted text must remain in the begin input as the parsed value");

		// Widget-side state should also reflect the parsed date.
		Long beginMs = (Long) js().executeScript(
				"var b = zk.Widget.$(jq('$drFormat')[0])._beginValue;"
				+ "return b ? b.getTime() : null;");
		assertNotNull(beginMs,
				"after paste+change, widget _beginValue must be set");
	}

	// ===== Auto-apply on range completion =====

	/** UX improvement: when the user picks the second date that completes
	 *  the range, the popup auto-applies and closes — no need to find the
	 *  Apply button. */
	@Test
	public void testAutoApplyOnRangeCompletion() {
		connect("/test2/F104-ZK-4305-attributes.zul");
		waitResponse();

		click(jq("$drFormat .z-daterangebox-button"));
		waitResponse();

		// First click: only begin staged — popup must stay open.
		js().executeScript(
				"function pick(p, day) {"
				+ "  var pane = p.$n();"
				+ "  var cells = pane.querySelectorAll('td.z-calendar-cell');"
				+ "  for (var i=0;i<cells.length;i++) {"
				+ "    if (jq(cells[i]).data('value') === day && (cells[i]._monofs||0) === 0) {"
				+ "      p._clickDate({target: cells[i], domTarget: cells[i], stop: function(){}});"
				+ "      return;"
				+ "    }"
				+ "  }"
				+ "}"
				+ "var box = zk.Widget.$(jq('$drFormat')[0]);"
				+ "pick(box._rangePopup._panels[0], 10);");
		waitResponse();
		String displayAfter1 = (String) js().executeScript(
				"return document.querySelector('.z-daterangebox-popup').style.display;");
		assertFalse("none".equals(displayAfter1),
				"popup must stay open after only begin is picked");

		// Second click: range complete — popup must auto-apply + close
		// within ~300ms (we use 200ms delay).
		js().executeScript(
				"function pick(p, day) {"
				+ "  var pane = p.$n();"
				+ "  var cells = pane.querySelectorAll('td.z-calendar-cell');"
				+ "  for (var i=0;i<cells.length;i++) {"
				+ "    if (jq(cells[i]).data('value') === day && (cells[i]._monofs||0) === 0) {"
				+ "      p._clickDate({target: cells[i], domTarget: cells[i], stop: function(){}});"
				+ "      return;"
				+ "    }"
				+ "  }"
				+ "}"
				+ "var box = zk.Widget.$(jq('$drFormat')[0]);"
				+ "pick(box._rangePopup._panels[0], 15);");
		waitForPopupClosed();
		waitResponse();

		String displayAfter2 = (String) js().executeScript(
				"return document.querySelector('.z-daterangebox-popup').style.display;");
		assertEquals("none", displayAfter2,
				"popup must auto-close after the second cell click completes the range");

		// Inputs must be populated (apply was called).
		String beginVal = (String) js().executeScript(
				"return jq('$drFormat .z-daterangebox-begin')[0].value;");
		String endVal = (String) js().executeScript(
				"return jq('$drFormat .z-daterangebox-end')[0].value;");
		assertFalse(beginVal == null || beginVal.isEmpty(),
				"auto-apply must populate begin input");
		assertFalse(endVal == null || endVal.isEmpty(),
				"auto-apply must populate end input");
	}

	// ===== Layout polish =====

	/** Layout: with buttonVisible=false the begin/end inputs together
	 *  must fill the box width — no dead space on the right where the
	 *  icon used to be. */
	@Test
	public void testButtonVisibleFalseInputsFillBox() {
		connect("/test2/F104-ZK-4305-attributes.zul");
		waitResponse();

		Long boxWidth = (Long) js().executeScript(
				"return Math.round(jq('$drNoButton')[0].getBoundingClientRect().width);");
		Long inputsWidth = (Long) js().executeScript(
				"var box = jq('$drNoButton')[0];"
				+ "var bi = box.querySelector('.z-daterangebox-begin').getBoundingClientRect();"
				+ "var sep = box.querySelector('.z-daterangebox-separator').getBoundingClientRect();"
				+ "var ei = box.querySelector('.z-daterangebox-end').getBoundingClientRect();"
				+ "return Math.round(bi.width + sep.width + ei.width);");
		// Allow a small slack for borders/padding rounding.
		long delta = Math.abs(boxWidth - inputsWidth);
		assertTrue(delta <= 4,
				"begin+sep+end must consume the full box width when no icon "
				+ "(box=" + boxWidth + ", inputs+sep=" + inputsWidth + ")");
	}

	/** Layout: rows with vs without the icon must have the same height so
	 *  forms don't visually jitter row-to-row. */
	@Test
	public void testRowHeightConsistentAcrossButtonVisible() {
		connect("/test2/F104-ZK-4305-attributes.zul");
		waitResponse();

		Long withIcon = (Long) js().executeScript(
				"return Math.round(jq('$drFormat')[0].getBoundingClientRect().height);");
		Long withoutIcon = (Long) js().executeScript(
				"return Math.round(jq('$drNoButton')[0].getBoundingClientRect().height);");
		assertEquals(withIcon, withoutIcon,
				"row height must match regardless of buttonVisible");
	}

	/** Layout: disabled state must render visibly distinct (different
	 *  background colour) from a writable box so users don't try to type. */
	@Test
	public void testDisabledHasDistinctBackground() {
		connect("/test2/F104-ZK-4305-attributes.zul");
		waitResponse();

		String enabledBg = (String) js().executeScript(
				"return getComputedStyle(jq('$drFormat')[0]).backgroundColor;");
		String disabledBg = (String) js().executeScript(
				"return getComputedStyle(jq('$drDisabled')[0]).backgroundColor;");
		assertFalse(enabledBg.equals(disabledBg),
				"disabled background must differ from enabled "
				+ "(enabled=" + enabledBg + ", disabled=" + disabledBg + ")");
	}

	/**
	 * timeZone and weekOfYear must propagate to every Calendar panel inside
	 * the popup. Locale propagation is intentionally deferred — page locale
	 * wins because zul.db.Calendar lacks a per-instance setLocale; Datebox
	 * carries the same limitation. This test pins down the two settings that
	 * *can* propagate.
	 */
	@Test
	public void testTimeZoneWeekOfYearPropagateToPanels() {
		connect("/test2/F104-ZK-4305-attributes.zul");
		waitResponse();

		click(jq("$drLocaleTzWeek .z-daterangebox-button"));
		waitResponse();
		assertTrue(jq(".z-daterangebox-popup").exists(),
				"Popup must open from $drLocaleTzWeek button");

		Boolean tzOk = (Boolean) js().executeScript(
				"var p = document.querySelectorAll('.z-daterangebox-popup .z-calendar');"
				+ "for (var i = 0; i < p.length; i++) {"
				+ "  var w = zk.Widget.$(p[i]);"
				+ "  if (!w || w._defaultTzone !== 'Asia/Tokyo') return false;"
				+ "} return p.length > 0;");
		assertEquals(Boolean.TRUE, tzOk,
				"Each Calendar panel must inherit timeZone=Asia/Tokyo from the box");

		Boolean weekOk = (Boolean) js().executeScript(
				"var p = document.querySelectorAll('.z-daterangebox-popup .z-calendar');"
				+ "for (var i = 0; i < p.length; i++) {"
				+ "  var w = zk.Widget.$(p[i]);"
				+ "  if (!w || !w._weekOfYear) return false;"
				+ "} return p.length > 0;");
		assertEquals(Boolean.TRUE, weekOk,
				"Each Calendar panel must inherit weekOfYear=true from the box");
	}

	/**
	 * When `showTime="true"` is set without an explicit
	 * `format`, the input must display the time the user picks. Server
	 * pushes the effective format (`yyyy/MM/dd HH:mm` derived from locale
	 * MEDIUM + auto-appended `HH:mm`), so the client widget's `_format`
	 * must contain `HH` and `mm` for time-bearing inputs.
	 */
	@Test
	public void testShowTimeAutoAppendsTimeFormatToInput() {
		connect("/test2/F104-ZK-4305-attributes.zul");
		waitResponse();

		String fmt = (String) js().executeScript(
				"return zk.Widget.$('$drShowTimeNoFmt')._format;");
		assertNotNull(fmt, "Client widget must receive the resolved format");
		assertTrue(fmt.contains("HH") || fmt.contains("kk") || fmt.contains("HH:mm"),
				"showTime=true without explicit format must auto-append HH:mm — got: " + fmt);
	}

	/**
	 * buttonVisible: even when the icon button is hidden, the
	 * box keeps a 32px row height so dense form layouts stay aligned.
	 */
	@Test
	public void testButtonVisibleFalseKeepsMinHeight() {
		connect("/test2/F104-ZK-4305-attributes.zul");
		waitResponse();

		Long height = (Long) js().executeScript(
				"return Math.round(jq('$drNoButton')[0].getBoundingClientRect().height);");
		assertTrue(height >= 32L,
				"buttonVisible=false box must keep >= 32px height; got " + height);
	}

	/**
	 * position: `vertical_center` and any token beyond the 5-token fast-path
	 * must still position
	 * the popup somewhere visible — silently falling through to default
	 * was the prior bug. We don't pin to exact coordinates (those depend
	 * on viewport size and zk.position algorithm), only that the popup is
	 * placed on-screen (its top-left corner inside the viewport and it does
	 * not overflow to the right). We do NOT require full vertical containment:
	 * `vertical_center` centres the popup on its anchor, so a tall popup whose
	 * anchor sits low on the page may extend a few px past the viewport bottom
	 * on a short browser window (e.g. bottom=940 vs innerHeight=937) while
	 * still being visible — that is not the "silently off-screen" bug.
	 */
	@Test
	public void testPositionVerticalCenterPlacesPopupInViewport() {
		connect("/test2/F104-ZK-4305-attributes.zul");
		waitResponse();

		click(jq("$drPosVCenter .z-daterangebox-button"));
		waitResponse();
		assertTrue(jq(".z-daterangebox-popup").exists(),
				"Popup must open for vertical_center positioned box");

		// On-screen check: top-left within the viewport, no right overflow, and
		// the popup starts above the fold (top < innerHeight). A tall popup may
		// spill a few px past the bottom on a short window — still visible, so
		// strict bottom containment would be a false failure.
		Boolean onScreen = (Boolean) js().executeScript(
				"var p = document.querySelector('.z-daterangebox-popup');"
				+ "var r = p.getBoundingClientRect();"
				+ "return r.top >= 0 && r.left >= 0"
				+ "    && r.right <= window.innerWidth + 1"
				+ "    && r.top < window.innerHeight;");
		String dbg = (String) js().executeScript(
				"var p = document.querySelector('.z-daterangebox-popup');"
				+ "var r = p.getBoundingClientRect();"
				+ "return 'rect{top='+Math.round(r.top)+',left='+Math.round(r.left)"
				+ "+',right='+Math.round(r.right)+',bottom='+Math.round(r.bottom)"
				+ "+'} viewport{w='+window.innerWidth+',h='+window.innerHeight+'}';");
		assertEquals(Boolean.TRUE, onScreen,
				"vertical_center popup must be placed on-screen, not silently off-screen [" + dbg + "]");
	}

	/**
	 * Regression: with {@code buttonVisible="false"} the trigger button is
	 * display:none, so close()'s back.focus() onto it silently no-ops and
	 * focus lands on &lt;body&gt;. The fix routes the focus fallback through
	 * the begin input, which is always focusable.
	 */
	@Test
	public void testButtonVisibleFalseFocusFallsBackToBeginInput() {
		connect("/test2/F104-ZK-4305-attributes.zul");
		waitResponse();

		// Open via the (always-focusable) begin input so we can observe the
		// post-Cancel focus restoration target.
		click(jq("$drNoButton .z-daterangebox-begin"));
		waitResponse();
		assertTrue(jq(".z-daterangebox-popup").exists(),
				"Popup must open for buttonVisible=false box via input click");

		click(jq(".z-daterangebox-popup-cancel"));
		waitResponse();

		String activeId = (String) js().executeScript(
				"return document.activeElement && document.activeElement.id;");
		Boolean isInput = (Boolean) js().executeScript(
				"var a = document.activeElement;"
				+ "return !!(a && a.classList && a.classList.contains('z-daterangebox-input'));");
		assertEquals(Boolean.TRUE, isInput,
				"After Cancel on buttonVisible=false box, focus must land on a "
				+ "daterangebox input (not <body>); got activeElement id="
				+ activeId);
	}

	/**
	 * Regression: a programmatic {@code applyRange(later, earlier, true)} from
	 * the client must be rejected server-side. {@code applyRange} documents
	 * "no auto-swap"; the rejected pair must NOT end up as committed state.
	 *
	 * <p>This test pins down the contract by asserting that the rejected
	 * begin value (2026-06-30) does not appear in either {@code _beginValue}
	 * or {@code _endValue} after the round-trip. The server's snap-back
	 * pushes whatever state it had pre-rejection — for a freshly-loaded
	 * box that's {@code null}, which is the correct rejection outcome (the
	 * commit never landed).
	 */
	@Test
	public void testApplyRangeReversedRejectedByServer() {
		connect("/test2/F104-ZK-4305-attributes.zul");
		waitResponse();

		final long rejectedBeginMs = (Long) js().executeScript(
				"return new Date(2026, 5, 30).getTime();");

		js().executeScript(
				"var w = zk.Widget.$('$drFormat');"
				+ "w.applyRange(new Date(2026, 5, 30), new Date(2026, 0, 5), true);");
		waitResponse();

		Long beginMs = (Long) js().executeScript(
				"var v = zk.Widget.$('$drFormat')._beginValue;"
				+ "return v ? v.getTime() : null;");
		Long endMs = (Long) js().executeScript(
				"var v = zk.Widget.$('$drFormat')._endValue;"
				+ "return v ? v.getTime() : null;");
		// The reversed pair must not have committed. Server-side
		// validateCandidateOrThrow throws, then snap-back smartUpdates push
		// the server's pre-call state (null for a fresh drFormat box).
		// Critically, neither endpoint should equal the rejected begin date.
		if (beginMs != null) {
			assertNotEquals((Long) rejectedBeginMs, beginMs,
					"Rejected begin must not commit to _beginValue");
		}
		if (endMs != null) {
			assertNotEquals((Long) rejectedBeginMs, endMs,
					"Rejected begin must not commit to _endValue");
		}
		// And when both are present, they must satisfy begin <= end (the
		// canonical invariant the server enforces).
		if (beginMs != null && endMs != null) {
			assertTrue(beginMs <= endMs,
					"After server-side rejection, snap-back must leave a "
					+ "non-reversed range; got begin=" + beginMs + " end=" + endMs);
		}
	}

	/**
	 * Regression: {@code validateCandidateOrThrow}'s reverse-range check
	 * has two branches. With {@code showTime=false} (the default), the
	 * check compares calendar days, so a same-day pair with reversed
	 * wall-clock times must NOT be rejected.
	 */
	@Test
	public void testReverseCheck_noShowTime_sameDayReversedAccepted() {
		connect("/test2/F104-ZK-4305-attributes.zul");
		waitResponse();

		// drFormat has no showTime — exercises the LocalDate branch.
		// Two timestamps on the same calendar day, with begin > end wall-clock.
		js().executeScript(
				"var w = zk.Widget.$('$drFormat');"
				+ "w.applyRange("
				+ "  new Date(2026, 5, 15, 23, 0),"  // Jun 15 23:00
				+ "  new Date(2026, 5, 15, 9, 0),"   // Jun 15 09:00
				+ "  true);");
		waitResponse();

		Long beginMs = (Long) js().executeScript(
				"var v = zk.Widget.$('$drFormat')._beginValue;"
				+ "return v ? v.getTime() : null;");
		// Day-strict reverse-check accepts same-day reversed wall-clock:
		// the server should commit the value, not snap back.
		assertNotNull(beginMs,
				"showTime=false branch must accept same-day pair regardless "
				+ "of wall-clock order; got null (snap-back triggered)");
	}

	/**
	 * Regression: with {@code showTime=true} the reverse-check uses wall-clock
	 * {@code Date.after}, so a same-day pair with reversed times MUST be
	 * rejected (the user picked an explicit time-of-day, inversion is
	 * intentional input not a granularity artefact).
	 */
	@Test
	public void testReverseCheck_showTime_sameDayReversedRejected() {
		connect("/test2/F104-ZK-4305-attributes.zul");
		waitResponse();

		final long rejectedBeginMs = (Long) js().executeScript(
				"return new Date(2026, 5, 15, 23, 0).getTime();");

		js().executeScript(
				"var w = zk.Widget.$('$drShowTime');"
				+ "w.applyRange("
				+ "  new Date(2026, 5, 15, 23, 0),"
				+ "  new Date(2026, 5, 15, 9, 0),"
				+ "  true);");
		waitResponse();

		Long beginMs = (Long) js().executeScript(
				"var v = zk.Widget.$('$drShowTime')._beginValue;"
				+ "return v ? v.getTime() : null;");
		// showTime=true treats the times as authoritative: a reversed pair
		// is genuine bad input, the server must reject and snap-back.
		if (beginMs != null) {
			assertNotEquals((Long) rejectedBeginMs, beginMs,
					"showTime=true branch must reject same-day reversed "
					+ "wall-clock pair (begin=23:00, end=09:00)");
		}
	}
}
