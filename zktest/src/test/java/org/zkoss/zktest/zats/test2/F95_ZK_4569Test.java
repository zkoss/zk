/* F95_ZK_4569Test.java

	Purpose:
		
	Description:
		
	History:
		Mon May 12 16:20:11 CST 2020, Created by jameschu

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;

/**
 * @author jameschu
 */
public class F95_ZK_4569Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		List<ComponentAgent> btns = desktop.queryAll("button");
		btns.forEach(ComponentAgent::click);
		List<String> zkLog = desktop.getZkLog();
		Assertions.assertEquals(4, zkLog.size());
		Assertions.assertEquals("Clicked", zkLog.get(0));
		Assertions.assertEquals("Clicked", zkLog.get(1));
		Assertions.assertEquals("Clickedb", zkLog.get(2));
		Assertions.assertEquals("ClickedG", zkLog.get(3));
	}
}
