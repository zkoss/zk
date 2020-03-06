/* F91_ZK_4523Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Mar 06 17:03:21 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;

/**
 * @author rudyhuang
 */
public class F91_ZK_4523Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();

		List<ComponentAgent> buttons = desktop.queryAll("button");
		buttons.get(0).click();
		buttons.get(1).click();
		Assert.assertArrayEquals(
				new String[] { "Greetings, John Smith!", "Greetings, John Smith!" },
				desktop.getZkLog().toArray()
		);
	}
}
