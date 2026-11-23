/* B96_ZK_5079Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Apr 20 14:22:36 CST 2022, Created by jameschu

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.fail;

import org.junit.Test;

import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;

/**
 * @author jameschu
 */
public class B96_ZK_5079Test extends ZATSTestCase {
	@Test
	public void test() {
		final DesktopAgent desktop = connect();
		try {
			desktop.query("#btn").click();
		} catch (Exception e) {
			e.printStackTrace();
			fail("Should not throw exception!");
		}
	}
}
