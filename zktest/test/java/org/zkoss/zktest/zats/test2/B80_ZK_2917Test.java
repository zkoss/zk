/* B80_ZK_2917Test.java

	Purpose:
		
	Description:
		
	History:
		12:20 PM 10/27/15, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static junit.framework.Assert.assertEquals;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

/**
 * @author jumperchen
 */
public class B80_ZK_2917Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		ComponentAgent cmdBtn = desktop.query("#cmdBtn");
		ComponentAgent addBtn = desktop.query("#addBtn");
		ComponentAgent rmBtn = desktop.query("#rmBtn");
		ComponentAgent msg = desktop.query("#msg");

		cmdBtn.click();
		assertEquals("inner command triggered", msg.as(Label.class).getValue());
		for (int i = 0; i < 3; i++) {
			rmBtn.click();
			assertEquals("",
					msg.as(Label.class).getValue());

			cmdBtn.click();
			assertEquals("inner command triggered",
					msg.as(Label.class).getValue());
			addBtn.click();
			assertEquals("",
					msg.as(Label.class).getValue());

			cmdBtn.click();
			assertEquals("inner command triggered",
					msg.as(Label.class).getValue());
			assertEquals("inner command triggered",
					msg.as(Label.class).getValue());
		}

	}
}
