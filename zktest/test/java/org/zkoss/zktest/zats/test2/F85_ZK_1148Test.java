/* B85_ZK_1148Test.java

        Purpose:
                
        Description:
                
        History:
                Fri Mar 16 6:09 PM:01 CST 2018, Created by klyve

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;


public class F85_ZK_1148Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent da = connect();
		ComponentAgent btn = da.query("#delBtn");
		btn.click();
		ComponentAgent label = da.query("#resultLabel");
		Assert.assertEquals("DestroyD DestroyC", label.as(Label.class).getValue().trim());
		
	}
}