/* B85_ZK_3815Test.java

        Purpose:
                
        Description:
                
        History:
                Tue Mar 27 15:58:13 CST 2018, Created by charlesqiu

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

public class B85_ZK_3815Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = null;
		try {
			desktop = connect();
		} catch (Exception e) {
			Assert.fail();
		}
		Assert.assertEquals(String.valueOf(Math.sqrt(16)), desktop.query("#lb1").as(Label.class).getValue());
		Assert.assertEquals(String.valueOf(Math.sqrt(16)), desktop.query("#lb2").as(Label.class).getValue());
	}
}
