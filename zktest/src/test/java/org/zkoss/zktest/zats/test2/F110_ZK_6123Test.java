/* F110_ZK_6123Test.java

        Purpose:

        Description:

        History:
                Wed Aug 19 10:24:51 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * The begin/end Daterangebox properties in the java.time types must save as well
 * as load under {@code @bind}: each test picks a range and reads the bound label.
 */
public class F110_ZK_6123Test extends WebDriverTestCase {

	private JavascriptExecutor js() {
		return (JavascriptExecutor) driver;
	}

	/** Load-only baseline: passes either way, catches a malformed ZKBIND declaration. */
	@Test
	public void testInitialLoad() {
		connect("/test2/F110-ZK-6123.zul");
		waitResponse();

		assertEquals(3, jq(".z-daterangebox").length(),
				"three bound daterangebox instances must render");
		// non-empty rather than exact, so the check does not depend on the date format
		for (String boxId : new String[] { "drLocalDate", "drLocalDateTime", "drZonedDateTime" }) {
			assertTrue(!inputValue(boxId, "begin").isEmpty(),
					boxId + " begin input must show the loaded date");
			assertTrue(!inputValue(boxId, "end").isEmpty(),
					boxId + " end input must show the loaded date");
		}
		assertEquals("2020-01-05", jq("$lblBeginLocalDate").text());
		assertEquals("2020-01-20", jq("$lblEndLocalDate").text());
		assertEquals("2020-02-05", jq("$lblBeginLocalDateTime").text());
		assertEquals("2020-02-20", jq("$lblEndLocalDateTime").text());
		assertEquals("2020-03-05", jq("$lblBeginZonedDateTime").text());
		assertEquals("2020-03-20", jq("$lblEndZonedDateTime").text());
	}

	@Test
	public void testLocalDateSaveRoundTrip() {
		connect("/test2/F110-ZK-6123.zul");
		waitResponse();

		pickRange("drLocalDate", 10, 15);
		awaitLabel("lblEndLocalDate", "2020-01-15");

		assertEquals("2020-01-10", jq("$lblBeginLocalDate").text(),
				"beginValueInLocalDate must save into vm.beginLocalDate; the label still shows the initial date");
		assertEquals("2020-01-15", jq("$lblEndLocalDate").text(),
				"endValueInLocalDate must save into vm.endLocalDate; the label still shows the initial date");
	}

	@Test
	public void testLocalDateTimeSaveRoundTrip() {
		connect("/test2/F110-ZK-6123.zul");
		waitResponse();

		pickRange("drLocalDateTime", 10, 15);
		awaitLabel("lblEndLocalDateTime", "2020-02-15");

		assertEquals("2020-02-10", jq("$lblBeginLocalDateTime").text(),
				"beginValueInLocalDateTime must save into vm.beginLocalDateTime; the label still shows the initial date");
		assertEquals("2020-02-15", jq("$lblEndLocalDateTime").text(),
				"endValueInLocalDateTime must save into vm.endLocalDateTime; the label still shows the initial date");
	}

	@Test
	public void testZonedDateTimeSaveRoundTrip() {
		connect("/test2/F110-ZK-6123.zul");
		waitResponse();

		pickRange("drZonedDateTime", 10, 15);
		awaitLabel("lblEndZonedDateTime", "2020-03-15");

		assertEquals("2020-03-10", jq("$lblBeginZonedDateTime").text(),
				"beginValueInZonedDateTime must save into vm.beginZonedDateTime; the label still shows the initial date");
		assertEquals("2020-03-15", jq("$lblEndZonedDateTime").text(),
				"endValueInZonedDateTime must save into vm.endZonedDateTime; the label still shows the initial date");
	}

	private String inputValue(String boxId, String side) {
		return (String) js().executeScript(
				"return jq('$" + boxId + " .z-daterangebox-" + side + "')[0].value;");
	}

	/** Picks two days on the first panel, which the popup anchors on the box's
	 *  current begin value — so the picks land in the ViewModel's initial month. */
	private void pickRange(String boxId, int beginDay, int endDay) {
		click(jq("$" + boxId + " .z-daterangebox-button"));
		waitResponse();
		// each Calendar repaints on a microtask, so the grid can still show "today":
		// wait for the begin highlight that means panel 0 has repainted
		new WebDriverWait(driver, Duration.ofSeconds(5)).until(d -> Boolean.TRUE.equals(
				js().executeScript(
						"var b = zk.Widget.$(jq(arguments[0])[0]);"
						+ "var p = b._rangePopup && b._rangePopup._panels[0];"
						+ "return !!p && !!p.$n() && !!p.$n().querySelector('.z-cell-range-begin');",
						"$" + boxId)));
		js().executeScript(
				"var box = zk.Widget.$(jq(arguments[0])[0]);"
				+ "var panel = box._rangePopup._panels[0];"
				+ "function pick(day) {"
				+ "  var cells = panel.$n().querySelectorAll('td.z-calendar-cell');"
				+ "  for (var i=0;i<cells.length;i++) {"
				+ "    if (jq(cells[i]).data('value') === day && (cells[i]._monofs||0) === 0) {"
				+ "      cells[i].dispatchEvent(new MouseEvent('click', {bubbles: true, cancelable: true}));"
				+ "      return;"
				+ "    }"
				+ "  }"
				+ "}"
				+ "pick(arguments[1]); pick(arguments[2]);",
				"$" + boxId, beginDay, endDay);
		waitResponse();
	}

	/** Polls out the 200ms auto-apply plus the save/reload round-trip. A timeout is
	 *  swallowed so the caller's assertion reports the actual label text. */
	private void awaitLabel(String labelId, String expected) {
		try {
			new WebDriverWait(driver, Duration.ofSeconds(5))
					.until(d -> expected.equals(jq("$" + labelId).text()));
		} catch (TimeoutException ex) {
			// fall through — the assertion in the caller reports what was shown
		}
	}
}
