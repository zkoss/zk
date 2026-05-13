/* F104_ZK_6097_AvatargroupMVVMTest.java

		Purpose:

		Description:

		History:
				Wed May 13 13:17:28 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class F104_ZK_6097_AvatargroupMVVMTest extends WebDriverTestCase {

	@Override
	protected String getFileLocation() {
		return "/test2/F104-ZK-6097-Avatargroup-MVVM.zul";
	}

	@Test
	public void testInitialOverflow() {
		connect();
		waitResponse();
		JQuery ag = jq("$agMvvm");
		assertTrue(ag.exists(), "agMvvm not found");
		// maxCount=3 initial, 5 children → overflow "+2"
		JQuery overflow = ag.find(".z-avatargroup-overflow");
		assertTrue(overflow.exists(), "initial overflow indicator expected");
		assertTrue(overflow.text().trim().startsWith("+"), "overflow text should start with +");
	}

	@Test
	public void testRemoveLimit() {
		connect();
		waitResponse();
		click(jq("$btnRemoveLimit"));
		waitResponse();
		JQuery ag = jq("$agMvvm");
		assertFalse(ag.find(".z-avatargroup-overflow").exists(),
				"overflow should be gone after removing limit");
	}

	@Test
	public void testSetLimit2() {
		connect();
		waitResponse();
		click(jq("$btnSetLimit2"));
		waitResponse();
		JQuery overflow = jq("$agMvvm").find(".z-avatargroup-overflow");
		assertTrue(overflow.exists(), "overflow should appear after setLimit2");
		assertTrue(overflow.text().trim().startsWith("+3"), "overflow should be +3");
	}

	@Test
	public void testSetLargeSquare() {
		connect();
		waitResponse();
		click(jq("$btnSetLargeSquare"));
		waitResponse();
		JQuery ag = jq("$agMvvm");
		assertTrue(ag.hasClass("z-avatargroup-large"), "expected large class after setLargeSquare");
		assertTrue(ag.hasClass("z-avatargroup-square"), "expected square class after setLargeSquare");
	}
}
