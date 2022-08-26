/* B96_ZK_4889Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Jul 6 18:44:32 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
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
public class B96_ZK_4889Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		List<ComponentAgent> btns = desktop.queryAll("button");
		btns.forEach(ComponentAgent::click);
		List<String> zkLog = desktop.getZkLog();
		Assertions.assertEquals(9, zkLog.size());
		Assertions.assertEquals("test1 param - number: -1", zkLog.get(0));
		Assertions.assertEquals("test1 param - number: 2", zkLog.get(1));
		Assertions.assertEquals("test1 param - number: -1", zkLog.get(2));
		Assertions.assertEquals("test1 param - number: -1", zkLog.get(3));
		Assertions.assertEquals("test1 param - number: 2", zkLog.get(4));
		Assertions.assertEquals("test2 param - number: -1", zkLog.get(5));
		Assertions.assertEquals("test2 param - number: 2", zkLog.get(6));
		Assertions.assertEquals("test3 param - count: 1, param - number: -1", zkLog.get(7));
		Assertions.assertEquals("test3 param - count: 1, param - number: 1", zkLog.get(8));
	}
}
