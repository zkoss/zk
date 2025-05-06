/* B102_ZK_5904Test.java

	Purpose:

	Description:

	History:
		Tue Apr 29 10:55:43 CST 2025, Created by jameschu

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;

/**
 * @author jameschu
 */
public class B102_ZK_5904Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktopAgent = connect();
		List<ComponentAgent> buttons = desktopAgent.queryAll("button");
		buttons.get(0).click();
		buttons.get(1).click();
		List<String> zkLog = desktopAgent.getZkLog();
		assertEquals("doCommand -> 123", zkLog.get(0));
		assertEquals("doCommand -> 123, 456", zkLog.get(1));
	}
}
