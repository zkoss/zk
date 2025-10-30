/* B96_ZK_4903Test.java

	Purpose:
		
	Description:
		
	History:
		Fri May 21 14:50:22 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zkmax.zul.GoldenPanel;
import org.zkoss.zktest.zats.ZATSTestCase;

/**
 * @author jameschu
 */
public class B96_ZK_4903Test extends ZATSTestCase {
	@Test
	public void test() throws Exception {
		DesktopAgent desktopAgent = connect();
		assertEquals("3", desktopAgent.query("#gpA").as(GoldenPanel.class).getVflex());
		assertEquals("1", desktopAgent.query("#gpC").as(GoldenPanel.class).getVflex());
	}
}
