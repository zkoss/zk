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
import org.zkoss.zktest.test2.F85_ZK_1148FileDealer;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;


public class F85_ZK_1148Test extends ZATSTestCase {

	@Test
	public void testInnerWindowDetach() {
		DesktopAgent da = connect();
		ComponentAgent wout = da.query("#outWin");
		ComponentAgent btn = wout.query("#delBtn");
		btn.click();
		ComponentAgent label = da.query("#resultLabel");
		Assert.assertEquals("DestroyD DestroyC", label.as(Label.class).getValue().trim());
	}

	@Test
	public void testOutWindowDetach() {
		DesktopAgent da = connect();
		ComponentAgent btn = da.query("#delBtno");
		btn.click();
		ComponentAgent label = da.query("#resultLabel");
		Assert.assertEquals("DestroyD DestroyC", label.as(Label.class).getValue().trim());

	}

	@Test
	public void testDesktopDestroy() {
		DesktopAgent da = connect();
		da.destroy();
		Assert.assertEquals("DestroyD is called!DestroyC is called!", F85_ZK_1148FileDealer.readMsg());
		F85_ZK_1148FileDealer.close();
	}


}