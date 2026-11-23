/* C2Test.java

	Purpose:
		
	Description:
		
	History:
		Mon Apr 26 11:52:03 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.collection;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;

/**
 * @author rudyhuang
 */
public class C2Test extends ZATSTestCase {
	@Test
	public void test() {
		final DesktopAgent desktop = connect();

		for (int i = 4; i >= 0; i--) {
			desktop.query("button").click();
			Assert.assertEquals(i, desktop.queryAll("listitem").size());
		}
	}
}
