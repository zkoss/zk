/* F95_ZK_4552Test.java

	Purpose:
		
	Description:
		
	History:
		Mon Apr 21 12:03:21 CST 2020, Created by jameschu

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

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
		Assertions.assertEquals(2, zkLog.size());
		Assertions.assertEquals("do click!!123,asssas,sa2,1", zkLog.get(0));
		Assertions.assertEquals("do test!!aa,12,2", zkLog.get(1));
		ComponentAgent label1 = desktop.query("#l1");
		Assertions.assertEquals("a", label1.as(Label.class).getValue());
		ComponentAgent label2 = desktop.query("#l2");
		Assertions.assertEquals("bbb", label2.as(Label.class).getValue());
		ComponentAgent div = desktop.query("#list");
		Assertions.assertEquals(6, div.getChildren().size());
	}

	@Test
	public void testNested() {
		DesktopAgent desktop = connect("/test2/F95-ZK-4552-nested.zul");
		List<ComponentAgent> btns = desktop.queryAll("button");
		btns.forEach(ComponentAgent::click);
		List<String> zkLog = desktop.getZkLog();
		Assertions.assertEquals(3, zkLog.size());
		Assertions.assertEquals("do click!!123,asssas,sa2,1", zkLog.get(0));
		Assertions.assertEquals("do test!!aa,12,2", zkLog.get(1));
		Assertions.assertEquals("nested clicked!", zkLog.get(2));
		ComponentAgent label1 = desktop.query("#l1");
		Assertions.assertEquals("a", label1.as(Label.class).getValue());
		ComponentAgent label2 = desktop.query("#l2");
		Assertions.assertEquals("bbb", label2.as(Label.class).getValue());
		ComponentAgent div = desktop.query("#list");
		Assertions.assertEquals(6, div.getChildren().size());
		ComponentAgent label3 = desktop.query("#l3");
		Assertions.assertEquals("a1", label3.as(Label.class).getValue());
		ComponentAgent nested = desktop.query("#nested");
		Assertions.assertEquals(8, nested.getChildren().size());
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
