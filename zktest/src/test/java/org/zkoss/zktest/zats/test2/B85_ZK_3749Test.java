/* B85_ZK_3749Test.java

        Purpose:
                
        Description:
                
        History:
                Wed Jan 31 3:06 PM:15 CST 2018, Created by klyve

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;

public class B85_ZK_3749Test extends ZATSTestCase {
	@Test
	public void test() throws Exception {
		try {
			DesktopAgent desktop = connect();
		} catch (Exception e) {
			Assertions.fail();
		}
	}
}
