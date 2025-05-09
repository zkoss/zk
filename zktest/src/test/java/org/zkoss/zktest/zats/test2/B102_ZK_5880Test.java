/* B102_ZK_5880Test.java

	Purpose:

	Description:

	History:
		Tue Apr 29 10:55:43 CST 2025, Created by jameschu

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import java.util.List;

import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;

/**
 * @author jameschu
 */
public class B102_ZK_5880Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktopAgent = connect();
		List<ComponentAgent> buttons = desktopAgent.queryAll("button");
		for (ComponentAgent button : buttons) {
			button.click();
		}
	}
}
