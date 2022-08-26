/* B85_ZK_3871Test.java

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

public class B85_ZK_3871Test extends ZATSTestCase {

	@Test
	public void testNestedChoose() {
		try {
			DesktopAgent desktop = connect("/test2/B85-ZK-3871-choose.zul");

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

	@Test
	public void testNestedIf() {
		try {
			DesktopAgent desktop = connect("/test2/B85-ZK-3871-if.zul");

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

	@Test
	public void testNestedChooseIf() {
		try {
			DesktopAgent desktop = connect("/test2/B85-ZK-3871-chooseif.zul");

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

