/* B86_ZK_4195Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Jan 17 15:02:08 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import java.util.List;

import org.junit.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;

/**
 * @author rudyhuang
 */
public class B86_ZK_4195Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();

		List<ComponentAgent> buttons = desktop.queryAll("button");
		buttons.get(0).click();
		buttons.get(1).click();
	}
}
