/* F91_ZK_4569Test.java

	Purpose:
		
	Description:
		
	History:
		Mon May 12 16:20:11 CST 2020, Created by jameschu

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
 * @author jameschu
 */
public class F91_ZK_4569Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		List<ComponentAgent> btns = desktop.queryAll("button");
		btns.forEach(ComponentAgent::click);
		List<String> zkLog = desktop.getZkLog();
		Assert.assertEquals(4, zkLog.size());
		Assert.assertEquals("Clicked", zkLog.get(0));
		Assert.assertEquals("Clicked", zkLog.get(1));
		Assert.assertEquals("Clickedb", zkLog.get(2));
		Assert.assertEquals("ClickedG", zkLog.get(3));
	}
}
