/* F104_ZK_6097_ConfirmpopupMVCTest.java

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

public class F104_ZK_6097_ConfirmpopupMVCTest extends WebDriverTestCase {

	@Override
	protected String getFileLocation() {
		return "/test2/F104-ZK-6097-Confirmpopup-MVC.zul";
	}

	// @Listen onOK on Confirmpopup routes to Composer
	@Test
	public void mvc_onOK_routed_to_composer() {
		connect();
		waitResponse();
		assertEquals("pending", jq("$mvcResult").text());

		click(jq("$btnOpenMvc"));
		waitResponse();
		click(jq("$cpMvc .z-confirmpopup-ok"));
		waitResponse();
		assertEquals("confirmed", jq("$mvcResult").text(),
				"@Listen(onOK=#cpMvc) must route event to Composer method");
		assertFalse(jq("$cpMvc").isVisible(), "popup must close after OK");
	}

	// @Listen onCancel on Confirmpopup routes to Composer
	@Test
	public void mvc_onCancel_routed_to_composer() {
		connect();
		waitResponse();
		click(jq("$btnOpenMvc"));
		waitResponse();
		click(jq("$cpMvc .z-confirmpopup-cancel"));
		waitResponse();
		assertEquals("cancelled", jq("$mvcResult").text(),
				"@Listen(onCancel=#cpMvc) must route cancel event to Composer");
	}
}
