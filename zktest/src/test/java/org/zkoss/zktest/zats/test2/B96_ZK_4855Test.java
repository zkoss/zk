/* B96_ZK_4855Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Apr 20 15:50:21 CST 2021, Created by jameschu

Copyright (C) 2017 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;

/**
 * @author jameschu
 */
public class B96_ZK_4855Test extends ZATSTestCase {
	@Test
	public void test() throws Exception {
		DesktopAgent desktop = connect();
		desktop.query("#btn1").click();
		assertEquals(2, desktop.queryAll("listitem").size());
		desktop.query("#btn2").click();
		assertEquals(4, desktop.queryAll("listitem").size());
	}
}
