/* B86_ZK_4190_2Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Jan 17 10:54:46 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Test;

import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;

/**
 * @author rudyhuang
 */
public class B86_ZK_4190_2Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();

		desktop.query("#cst1").click();
		desktop.query("#trigger").click();

		desktop.query("#cst2").click();
		desktop.query("#trigger").click();
	}
}
