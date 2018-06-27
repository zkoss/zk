/* B85_ZK_3944Test.java

        Purpose:
                
        Description:
                
        History:
                Wed Jun 27 15:48:56 CST 2018, Created by charlesqiu

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;

public class B85_ZK_3944Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		try {
			desktop.query("#button").click();
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
}
