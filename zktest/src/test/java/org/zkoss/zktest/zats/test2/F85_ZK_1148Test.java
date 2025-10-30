/* B85_ZK_1148Test.java

        Purpose:
                
        Description:
                
        History:
                Fri Mar 16 6:09 PM:01 CST 2018, Created by klyve

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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
		Assertions.assertEquals("DestroyD DestroyC", label.as(Label.class).getValue().trim());
	}

	@Test
	public void testOutWindowDetach() {
		DesktopAgent da = connect();
		ComponentAgent btn = da.query("#delBtno");
		btn.click();
		ComponentAgent label = da.query("#resultLabel");
		Assertions.assertEquals("DestroyD DestroyC", label.as(Label.class).getValue().trim());

	}

	@Test
	public void testDesktopDestroy() {
		F85_ZK_1148FileDealer.writeMsg("", false);
		DesktopAgent da = connect();
		da.destroy();
		try {
			Assertions.assertEquals("DestroyD is called!DestroyC is called!IncludeVM is called!", F85_ZK_1148FileDealer.readMsg());
		} finally {
			F85_ZK_1148FileDealer.close();
		}
	}

	@Test
	public void testIncludeDestroy() {
		F85_ZK_1148FileDealer.writeMsg("", false);
		DesktopAgent da = connect();
		ComponentAgent reloadWin = da.query("#reloadWin");
		ComponentAgent btn = reloadWin.query("#reload");
		btn.click();
		try {
			Assertions.assertEquals("IncludeVM is called!", F85_ZK_1148FileDealer.readMsg());
		} finally {
			F85_ZK_1148FileDealer.close();
		}
	}

	@AfterAll
	public static void cleanUp() {
		F85_ZK_1148FileDealer.close();
	}

}
