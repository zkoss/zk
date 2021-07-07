/* B96_ZK_4889Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Jul 6 18:44:32 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

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
		Assert.assertEquals(9, zkLog.size());
		Assert.assertEquals("test1 param - number: -1", zkLog.get(0));
		Assert.assertEquals("test1 param - number: 2", zkLog.get(1));
		Assert.assertEquals("test1 param - number: -1", zkLog.get(2));
		Assert.assertEquals("test1 param - number: -1", zkLog.get(3));
		Assert.assertEquals("test1 param - number: 2", zkLog.get(4));
		Assert.assertEquals("test2 param - number: -1", zkLog.get(5));
		Assert.assertEquals("test2 param - number: 2", zkLog.get(6));
		Assert.assertEquals("test3 param - count: 1, param - number: -1", zkLog.get(7));
		Assert.assertEquals("test3 param - count: 1, param - number: 1", zkLog.get(8));
	}
}
