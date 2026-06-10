/* F104_ZK_4305_DaterangeBindingTest.java

        Purpose:
                
        Description:
                
        History:
                Fri May 08 15:14:28 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.zkoss.test.webdriver.WebDriverTestCase;

public class F104_ZK_4305_DaterangeBindingTest extends WebDriverTestCase {

	private JavascriptExecutor js() {
		return (JavascriptExecutor) driver;
	}

	@Test
	public void testBindingRender() {
		connect("/test2/F104-ZK-4305-binding.zul");
		waitResponse();

		assertEquals(2, jq(".z-daterangebox").length(),
				"Two bound daterangebox instances must render");
	}

	/** Round-trip the whole-range DateRange binding: pick a range in the
	 *  popup, verify the VM's DateRange field updates and the @load-bound
	 *  begin/end labels show the picked dates. Without the binding wired
	 *  through ZKBIND/LOAD_TYPE the labels would stay empty after the pick. */
	@Test
	public void testDateRangeBindingRoundTrip() {
		connect("/test2/F104-ZK-4305-binding.zul");
		waitResponse();

		// Pre-state: VM range is null, both labels empty.
		assertTrue(jq("$lblRangeBegin").text().isEmpty(),
				"begin label must start empty (vm.range is null on init)");
		assertTrue(jq("$lblRangeEnd").text().isEmpty(),
				"end label must start empty (vm.range is null on init)");

		// Open the drValue popup and pick day-10 (begin) + day-15 (end) on
		// panel 0. The auto-apply timer commits the range after the second
		// pick, triggering the @bind save path → VM.setRange → @load refresh.
		// Dispatch a real native click on each cell rather than calling the
		// private `_clickDate` directly — keeps the test resilient to internal
		// refactors of zul.db.Calendar's click handler signature.
		click(jq("$drValue .z-daterangebox-button"));
		waitResponse();
		js().executeScript(
				"var box = zk.Widget.$(jq('$drValue')[0]);"
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
				+ "pick(10); pick(15);");
		waitResponse();
		// Wait for the 200ms auto-apply timer + the @bind save round-trip
		// to populate the @load-bound labels — polling rather than fixed
		// sleep avoids flakes on slow CI agents.
		new WebDriverWait(driver, Duration.ofSeconds(3))
				.until(d -> !jq("$lblRangeBegin").text().isEmpty()
						&& !jq("$lblRangeEnd").text().isEmpty());

		// Post-state: both labels must be populated, proving VM.setRange
		// fired AND @load(vm.range.begin/end) re-rendered.
		String beginText = jq("$lblRangeBegin").text();
		String endText = jq("$lblRangeEnd").text();
		assertFalse(beginText.isEmpty(),
				"after the pick, vm.range.begin must be reflected in the label; was empty");
		assertFalse(endText.isEmpty(),
				"after the pick, vm.range.end must be reflected in the label; was empty");
	}
}
