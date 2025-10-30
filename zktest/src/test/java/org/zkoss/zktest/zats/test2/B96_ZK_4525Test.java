/* B96_ZK_4525Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Jul 06 15:28:26 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

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
		Assertions.assertTrue(jq("$tooltip").isVisible(), "Tooltip was hidden!");
		Assertions.assertTrue(jq("$editPopup").isVisible(),
				"Context popup was hidden!");
	}
}
