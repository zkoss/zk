/* MVP2MVVMTest.java

	Purpose:
		
	Description:
		
	History:
		Wed Apr 28 16:34:28 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.basic;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Textbox;

/**
 * @author rudyhuang
 */
public class MVP2MVVMTest extends ZATSTestCase {
	@Test
	public void test() {
		final DesktopAgent desktop = connect("/bind/basic/mvp2mvvm_mvp.zul");
		final List<ComponentAgent> buttonsOutside = desktop.queryAll("window > button");
		final ComponentAgent buttonInside = desktop.query("window #mWinA #innerToggle");
		final Textbox textbox = desktop.query("window #mWinA #textA").as(Textbox.class);

		buttonsOutside.get(0).click();
		Assertions.assertFalse(textbox.isDisabled());
		buttonsOutside.get(0).click();
		Assertions.assertTrue(textbox.isDisabled());
		buttonsOutside.get(1).click();
		Assertions.assertFalse(textbox.isDisabled());
		buttonsOutside.get(1).click();
		Assertions.assertTrue(textbox.isDisabled());

		buttonInside.click();
		Assertions.assertFalse(textbox.isDisabled());
		buttonInside.click();
		Assertions.assertTrue(textbox.isDisabled());
	}
}
