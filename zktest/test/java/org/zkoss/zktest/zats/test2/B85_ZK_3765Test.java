/* B85_ZK_3765Test.java

	Purpose:
		
	Description:
		
	History:
		Mon Jan 15 15:56:38 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;

/**
 * @author rudyhuang
 */
public class B85_ZK_3765Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		desktop.queryAll("button").get(1).click();

		ComponentAgent msgbox = desktop.query("window");
		Assert.assertNotNull("The message box not popped up.", msgbox);
	}
}
