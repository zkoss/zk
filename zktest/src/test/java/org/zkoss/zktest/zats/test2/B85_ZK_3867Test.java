/* B85_ZK_3867.java

        Purpose:
                
        Description:
                
        History:
                Thu Feb 22 12:48 PM:43 CST 2018, Created by klyve

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zk.ui.Component;
import org.zkoss.zktest.zats.ZATSTestCase;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class B85_ZK_3867Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();

		ComponentAgent testWindow = desktop.query("#testWindow");
		List<ComponentAgent> divs = testWindow.queryAll("div");
		Integer i = 1;
		for (ComponentAgent div: divs) {
			assertEquals(i.toString(), div.getId());
			i++;
		}
	}
}
