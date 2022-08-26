/* B95_ZK_4776Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Jan 22 09:50:20 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zkmax.zul.GoldenLayout;
import org.zkoss.zktest.zats.ZATSTestCase;

/**
 * @author jameschu
 */
public class B95_ZK_4776Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		assertEquals(1, desktop.queryAll("goldenlayout").size());
		desktop.query("button").click();
		assertEquals(true, desktop.queryAll("goldenlayout").get(0).as(GoldenLayout.class).isInitialized());
	}
}
