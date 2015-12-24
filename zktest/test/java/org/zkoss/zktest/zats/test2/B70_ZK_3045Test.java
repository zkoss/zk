/* B70_ZK_3045Test.java

	Purpose:
		
	Description:
		
	History:
		11:13 AM 12/24/15, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

/**
 * @author jumperchen
 */
public class B70_ZK_3045Test extends ZATSTestCase {
	@Test
	public void testZK3045() {
		DesktopAgent desktopAgent = connect();
		ComponentAgent msg = desktopAgent.query("#msg");
		assertEquals("1", msg.as(Label.class).getValue());
		for (int i = 0; i < 5; i++) {
			desktopAgent.query("button").click();
			assertEquals("6", msg.as(Label.class).getValue());
		}
	}
}
