/* B96_ZK_4525Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Jul 06 15:28:26 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B96_ZK_4525Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		getActions().moveToElement(toElement(jq("@textbox")))
				.contextClick()
				.moveByOffset(-20, 0)
				.pause(2000)
				.perform();
		Assert.assertTrue("Tooltip was hidden!", jq("$tooltip").isVisible());
		Assert.assertTrue("Context popup was hidden!", jq("$editPopup").isVisible());
	}
}
