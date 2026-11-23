/* B86_ZK_4128Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Dec 11 17:45:49 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zk.ui.Component;
import org.zkoss.zktest.zats.ZATSTestCase;

/**
 * @author rudyhuang
 */
public class B86_ZK_4128Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();

		Component mylb = desktop.query("#mylb").as(Component.class);
		Assert.assertTrue("B86_ZK_4128Converter is not used.", mylb.hasAttribute("B86_ZK_4128Converter"));
	}
}
