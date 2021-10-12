/* F80_ZK_2951Test.java

	Purpose:
		
	Description:
		
	History:
		12:36 PM 11/12/15, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;

/**
 * @author jumperchen
 */
public class F80_ZK_2951Test extends ZATSTestCase {

	@Test
	public void test() {
		DesktopAgent desktop = connect();
		List<ComponentAgent> buttons = desktop.queryAll("button");

		try {
			buttons.get(0).click();
			buttons.get(1).click();
			assertTrue(true);
		} catch (Exception e) {
			fail();
		}


		try {
			buttons.get(2).click();
			fail();
		} catch (Exception e) {
			assertTrue(true);
		}
	}
}
