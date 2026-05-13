/* F104_ZK_6097_AvatarMVVMTest.java

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

public class F104_ZK_6097_AvatarMVVMTest extends WebDriverTestCase {

	@Override
	protected String getFileLocation() {
		return "/test2/F104-ZK-6097-Avatar-MVVM.zul";
	}

	// label @load binding: initial value rendered, @command updates it
	@Test
	public void mvvm_label_load_binding() {
		connect();
		waitResponse();
		// "AB" label renders as text inside the avatar
		assertTrue(jq("$avMvvm").text().contains("AB"),
				"@load(vm.avatarLabel) initial value 'AB' must render in avatar");

		click(jq("$btnShowLabel"));
		waitResponse();
		assertTrue(jq("$avMvvm").text().contains("JD"),
				"@command showLabel must update avatarLabel to 'JD'");
	}

	// shape @load binding: circle by default, @command changes to square
	@Test
	public void mvvm_shape_load_binding() {
		connect();
		waitResponse();
		assertEquals("circle", jq("$mvvmShape").text(), "initial shape = circle");
		assertFalse(jq("$avMvvm").hasClass("z-avatar-square"),
				"initial shape=circle must not have square class");

		click(jq("$btnSetSquare"));
		waitResponse();
		assertEquals("square", jq("$mvvmShape").text());
		assertTrue(jq("$avMvvm").hasClass("z-avatar-square"),
				"@command setSquare must apply z-avatar-square class");
	}

	// size @load binding: medium by default, @command changes to large
	@Test
	public void mvvm_size_load_binding() {
		connect();
		waitResponse();
		assertEquals("medium", jq("$mvvmSize").text(), "initial size = medium");

		click(jq("$btnSetLarge"));
		waitResponse();
		assertEquals("large", jq("$mvvmSize").text(),
				"@command setLarge must update size to large");
		assertTrue(jq("$avMvvm").hasClass("z-avatar-large"),
				"@load(vm.size) must apply z-avatar-large class");
	}
}
