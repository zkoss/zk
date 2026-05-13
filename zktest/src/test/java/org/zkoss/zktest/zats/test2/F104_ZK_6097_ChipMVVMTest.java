/* F104_ZK_6097_ChipMVVMTest.java

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

public class F104_ZK_6097_ChipMVVMTest extends WebDriverTestCase {

	@Override
	protected String getFileLocation() {
		return "/test2/F104-ZK-6097-Chip-MVVM.zul";
	}

	// severity @load binding propagates ViewModel -> UI
	@Test
	public void mvvm_severity_load_binding() {
		connect();
		waitResponse();
		assertEquals("info", jq("$mvvmSeverity").text(), "VM severity 'info' must be reflected in label");
		assertTrue(jq("$chipMvvm").hasClass("z-chip-info"),
				"@load(vm.severity) must set the CSS class on the chip");

		click(jq("$btnWarning"));
		waitResponse();
		assertEquals("warning", jq("$mvvmSeverity").text(), "label must update after @command changeSeverity");
		assertTrue(jq("$chipMvvm").hasClass("z-chip-warning"),
				"chip CSS class must change when VM severity changes via @command");
		assertFalse(jq("$chipMvvm").hasClass("z-chip-info"), "old severity class must be removed");
	}

	// label @load binding propagates ViewModel -> UI
	@Test
	public void mvvm_label_load_binding() {
		connect();
		waitResponse();
		assertEquals("MVVM Chip", jq("$chipMvvm").text().trim(), "@load label must render VM chipLabel");

		click(jq("$btnChangeLabel"));
		waitResponse();
		assertEquals("Updated Label", jq("$chipMvvm").text().trim(),
				"@NotifyChange(chipLabel) must push updated label to DOM");
	}

	// onClose fires @command -> VM result field -> @load label updates
	@Test
	public void mvvm_onClose_command_fires_and_updates_result() {
		connect();
		waitResponse();
		assertEquals("open", jq("$mvvmResult").text(), "initial result must be 'open'");

		click(jq("$chipMvvm .z-chip-close"));
		waitResponse();
		assertEquals("closed", jq("$mvvmResult").text(),
				"onClose @command must update result to 'closed' and @load must push it to label");
	}
}
