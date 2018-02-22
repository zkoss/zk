/* B85_ZK_3881Test.java

        Purpose:
                
        Description:
                
        History:
                Mon Feb 26 3:05 PM:01 CST 2018, Created by klyve

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;

import java.util.List;

import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;

public class B85_ZK_3881Test extends ZATSTestCase{

	@Test
	public void testNestedIf() {
		try {
			DesktopAgent desktop = connect("/test2/B85-ZK-3881-if.zul");

			List<ComponentAgent> buttons = desktop.queryAll("button");
			assertEquals(buttons.size(), 5);
			ComponentAgent chgBtn = buttons.get(0);
			ComponentAgent bckBtn = buttons.get(1);

			chgBtn.click();
			buttons = desktop.queryAll("button");
			assertEquals(buttons.size(), 7);

			bckBtn.click();
			buttons = desktop.queryAll("button");
			assertEquals(buttons.size(), 5);

		} catch (Exception e) {
			fail();
		}
	}
}
