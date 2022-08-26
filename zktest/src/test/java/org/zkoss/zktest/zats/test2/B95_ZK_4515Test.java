/* B95_ZK_4515Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Aug 28 12:41:09 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Button;

/**
 * @author rudyhuang
 */
public class B95_ZK_4515Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();

		desktop.queryAll("toolbarbutton").get(0).click();
		desktop.queryAll("textbox").get(1).type("Sun");
		desktop.queryAll("button").get(1).click();

		Assertions.assertTrue(
				desktop.query("button").as(Button.class).isDisabled(),
				"The button should be disabled");
	}
}
