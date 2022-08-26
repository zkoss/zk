/* B85_ZK_3630Test.java

        Purpose:
                
        Description:
                
        History:
                Tue Feb 13 3:03 PM:09 CST 2018, Created by klyve

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;

import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;

public class B85_ZK_3630Test extends ZATSTestCase {

	@Test
	public void test() {

		try {
			DesktopAgent desktop = connect();

			List<ComponentAgent> buttons = desktop.queryAll("button");
			assertEquals(8, buttons.size());
			ComponentAgent chgBtn = buttons.get(0);
			ComponentAgent bckBtn = buttons.get(1);

			chgBtn.click();
			buttons = desktop.queryAll("button");
			assertEquals(12, buttons.size());

			bckBtn.click();
			buttons = desktop.queryAll("button");
			assertEquals(8, buttons.size());

		} catch (Exception e) {
			fail();
		}
	}
}

