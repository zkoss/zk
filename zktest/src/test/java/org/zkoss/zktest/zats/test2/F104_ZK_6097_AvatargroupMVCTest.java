/* F104_ZK_6097_AvatargroupMVCTest.java

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

public class F104_ZK_6097_AvatargroupMVCTest extends WebDriverTestCase {

	@Override
	protected String getFileLocation() {
		return "/test2/F104-ZK-6097-Avatargroup-MVC.zul";
	}

	@Test
	public void testInitialOverflow() {
		connect();
		waitResponse();
		JQuery ag = jq("$agMvc");
		assertTrue(ag.exists(), "agMvc not found");
		assertTrue(ag.find(".z-avatargroup-overflow").exists(),
				"initial overflow indicator expected (maxCount=3, 5 children)");
	}

	@Test
	public void testRemoveLimit() {
		connect();
		waitResponse();
		click(jq("$btnRemoveLimit"));
		waitResponse();
		assertFalse(jq("$agMvc").find(".z-avatargroup-overflow").exists(),
				"overflow should disappear after removing limit");
	}

	@Test
	public void testAddAvatar() {
		connect();
		waitResponse();
		// Remove limit first so we can count visible avatars
		click(jq("$btnRemoveLimit"));
		waitResponse();
		int before = jq("$agMvc").find(".z-avatar").length();
		click(jq("$btnAddAvatar"));
		waitResponse();
		int after = jq("$agMvc").find(".z-avatar").length();
		assertTrue(after > before, "avatar count should increase after addAvatar");
	}

	@Test
	public void testSetSmallCircle() {
		connect();
		waitResponse();
		click(jq("$btnSetSmall"));
		waitResponse();
		JQuery ag = jq("$agMvc");
		assertTrue(ag.hasClass("z-avatargroup-small"), "expected small class");
		assertTrue(ag.hasClass("z-avatargroup-circle"), "expected circle class");
	}
}
