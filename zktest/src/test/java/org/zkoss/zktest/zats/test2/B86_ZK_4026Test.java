/* B86_ZK_4026Test.java

        Purpose:
                
        Description:
                
        History:
                Wed Aug 15 10:48:23 CST 2018, Created by klyve

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;

public class B86_ZK_4026Test extends ZATSTestCase {

	@Test
	public void test() {
		DesktopAgent da = connect();
		ComponentAgent btn = da.query("#notifyBtn");
		ComponentAgent lbl = da.query("#testLbl");
		String uuid1 = lbl.getUuid();
		btn.click();
		lbl = da.query("#testLbl");
		String uuid2 = lbl.getUuid();
		Assertions.assertEquals(uuid1, uuid2, "The two uuid should be the same:");
	}

}
