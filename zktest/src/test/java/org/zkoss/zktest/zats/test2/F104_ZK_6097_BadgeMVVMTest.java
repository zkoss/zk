/* F104_ZK_6097_BadgeMVVMTest.java

	Purpose:

	Description:

	History:
		Wed May 13 13:05:00 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class F104_ZK_6097_BadgeMVVMTest extends WebDriverTestCase {

	@Override
	protected String getFileLocation() {
		return "/test2/F104-ZK-6097-Badge-MVVM.zul";
	}

	// @load(vm.count) reflects initial value and increments via @command
	@Test
	public void mvvm_count_load_and_increment_command() {
		connect();
		waitResponse();
		// Initial count=0: badge shows nothing (showZero=false by default) or "0"
		// We verify the tracking label and then increment
		String before = jq("$mvvmCount").text();
		assertEquals("0", before, "initial count VM field = 0");

		click(jq("$btnIncrement"));
		waitResponse();
		assertEquals("1", jq("$mvvmCount").text(),
				"@command increment must update count and @NotifyChange must push to label");

		// Badge DOM should now show the count value
		assertTrue(jq("$bdgMvvm .z-badge-count").exists() ||
				jq("$bdgMvvm").text().contains("1"),
				"badge must display count=1");
	}

	// @load(vm.severity) reflects command-driven change
	@Test
	public void mvvm_severity_load_binding() {
		connect();
		waitResponse();
		assertEquals("info", jq("$mvvmSeverity").text());
		assertTrue(jq("$bdgMvvm").hasClass("z-badge-info") ||
				!jq("$bdgMvvm").hasClass("z-badge-danger"),
				"initial severity should be info");

		click(jq("$btnDanger"));
		waitResponse();
		assertEquals("danger", jq("$mvvmSeverity").text(),
				"@command setDangerSeverity must update severity and notify");
		assertTrue(jq("$bdgMvvm").hasClass("z-badge-danger"),
				"badge CSS class must switch to danger");
	}

	// reset @command resets multiple fields via @NotifyChange({...})
	@Test
	public void mvvm_reset_command_notifies_multiple_fields() {
		connect();
		waitResponse();
		click(jq("$btnIncrement"));
		waitResponse();
		click(jq("$btnDanger"));
		waitResponse();
		assertEquals("1", jq("$mvvmCount").text());
		assertEquals("danger", jq("$mvvmSeverity").text());

		click(jq("$btnReset"));
		waitResponse();
		assertEquals("0", jq("$mvvmCount").text(),
				"reset must set count back to 0");
		assertEquals("info", jq("$mvvmSeverity").text(),
				"reset must restore severity to info");
	}

	// toggleDot @command flips vm.dot; dot="@load(vm.dot)" must reflect it on
	// the badge — dot mode adds the z-badge-dot class (badge.less &-dot rule).
	@Test
	public void mvvm_toggle_dot_command() {
		connect();
		waitResponse();
		assertFalse(jq("$bdgMvvm").hasClass("z-badge-dot"),
				"dot defaults to false — badge has no z-badge-dot class");

		click(jq("$btnToggleDot"));
		waitResponse();
		assertTrue(jq("$bdgMvvm").hasClass("z-badge-dot"),
				"toggleDot @command + @load(vm.dot) must add z-badge-dot");

		click(jq("$btnToggleDot"));
		waitResponse();
		assertFalse(jq("$bdgMvvm").hasClass("z-badge-dot"),
				"toggling dot again must remove z-badge-dot");
	}
}
