/* F104_ZK_6097_ConfirmpopupMVVMTest.java

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

public class F104_ZK_6097_ConfirmpopupMVVMTest extends WebDriverTestCase {

	@Override
	protected String getFileLocation() {
		return "/test2/F104-ZK-6097-Confirmpopup-MVVM.zul";
	}

	// onOK fires @command -> VM result -> @load label updates
	@Test
	public void mvvm_onOK_command_updates_result() {
		connect();
		waitResponse();
		assertEquals("pending", jq("$mvvmResult").text());

		click(jq("$btnOpen"));
		waitResponse();
		click(jq("$cpMvvm .z-confirmpopup-ok"));
		waitResponse();
		assertEquals("confirmed", jq("$mvvmResult").text(),
				"@command onOK must update result to 'confirmed'");
		assertFalse(jq("$cpMvvm").isVisible(), "popup must close after OK");
	}

	// onCancel fires @command -> VM result -> @load label updates
	@Test
	public void mvvm_onCancel_command_updates_result() {
		connect();
		waitResponse();
		click(jq("$btnOpen"));
		waitResponse();
		click(jq("$cpMvvm .z-confirmpopup-cancel"));
		waitResponse();
		assertEquals("cancelled", jq("$mvvmResult").text(),
				"@command onCancel must update result to 'cancelled'");
	}
}
