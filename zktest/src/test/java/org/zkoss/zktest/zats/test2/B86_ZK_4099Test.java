/* B86_ZK_4099Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Oct 25 09:47:47 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;

/**
 * @author rudyhuang
 */
public class B86_ZK_4099Test extends ZATSTestCase {
	@Test
	public void test() {
		// Test FormProxyHandler
		DesktopAgent desktop = connect();

		// Test ViewModelProxyHandler
		desktop.query("button").click();
	}
}
