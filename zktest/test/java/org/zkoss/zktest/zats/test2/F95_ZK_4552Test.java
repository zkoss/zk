/* F95_ZK_4552Test.java

	Purpose:
		
	Description:
		
	History:
		Mon Apr 21 12:03:21 CST 2020, Created by jameschu

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

import static org.junit.Assert.fail;

/**
 * @author jameschu
 */
public class F95_ZK_4552Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect("/test2/F95-ZK-4552-syntax.zul");
		List<ComponentAgent> btns = desktop.queryAll("button");
		btns.forEach(ComponentAgent::click);
		List<String> zkLog = desktop.getZkLog();
		Assert.assertEquals(2, zkLog.size());
		Assert.assertEquals("do click!!123,asssas,sa2,1", zkLog.get(0));
		Assert.assertEquals("do test!!aa,12,2", zkLog.get(1));
		ComponentAgent label1 = desktop.query("#l1");
		Assert.assertEquals("a", label1.as(Label.class).getValue());
		ComponentAgent label2 = desktop.query("#l2");
		Assert.assertEquals("bbb", label2.as(Label.class).getValue());
		ComponentAgent div = desktop.query("#list");
		Assert.assertEquals(6, div.getChildren().size());
	}

	@Test
	public void testNested() {
		DesktopAgent desktop = connect("/test2/F95-ZK-4552-nested.zul");
		List<ComponentAgent> btns = desktop.queryAll("button");
		btns.forEach(ComponentAgent::click);
		List<String> zkLog = desktop.getZkLog();
		Assert.assertEquals(3, zkLog.size());
		Assert.assertEquals("do click!!123,asssas,sa2,1", zkLog.get(0));
		Assert.assertEquals("do test!!aa,12,2", zkLog.get(1));
		Assert.assertEquals("nested clicked!", zkLog.get(2));
		ComponentAgent label1 = desktop.query("#l1");
		Assert.assertEquals("a", label1.as(Label.class).getValue());
		ComponentAgent label2 = desktop.query("#l2");
		Assert.assertEquals("bbb", label2.as(Label.class).getValue());
		ComponentAgent div = desktop.query("#list");
		Assert.assertEquals(6, div.getChildren().size());
		ComponentAgent label3 = desktop.query("#l3");
		Assert.assertEquals("a1", label3.as(Label.class).getValue());
		ComponentAgent nested = desktop.query("#nested");
		Assert.assertEquals(8, nested.getChildren().size());
	}

	@Test
	public void testException() {
		try {
			connect("/test2/F95-ZK-4552-syntax-exception.zul");
		} catch (Exception e) {
			return;
		}
		fail("Should throw exception");
	}
}
