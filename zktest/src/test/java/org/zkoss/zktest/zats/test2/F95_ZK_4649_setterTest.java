/* F95_ZK_4649_setterTest.java

	Purpose:
		
	Description:
		
	History:
		Wed Sep 02 18:45:52 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Test;

import org.zkoss.zats.ZatsException;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;

/**
 * @author rudyhuang
 */
public class F95_ZK_4649_setterTest extends ZATSTestCase {
	@Test(expected = ZatsException.class)
	public void testShouldErrorIfSrclangIsEmpty() {
		DesktopAgent desktop = connect();

		desktop.queryAll("button").get(0).click();
	}

	@Test
	public void testShouldSuccessIfSrclangIsSet() {
		DesktopAgent desktop = connect();

		desktop.queryAll("button").get(1).click();
	}
}
