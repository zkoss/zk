/* F100_ZK_5018Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Dec 05 14:43:51 CST 2023, Created by jameschu

Copyright (C) 2023 Potix Corporation. All Rights Reserved.
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
import org.zkoss.zul.Textbox;

/**
 * @author jameschu
 */
public class F100_ZK_5018Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect("/test2/F100-ZK-5018-command.zul");
		List<ComponentAgent> btns = desktop.queryAll("button");
		btns.forEach(ComponentAgent::click);
		List<String> zkLog = desktop.getZkLog();
		Assertions.assertEquals(3, zkLog.size());
		Assertions.assertEquals("do click!!123,asssas,sa2,1", zkLog.get(0));
		Assertions.assertEquals("do test!!aa,12,2", zkLog.get(1));
		Assertions.assertEquals("logMessage, 123", zkLog.get(2));
		ComponentAgent label1 = desktop.query("#l1");
		Assertions.assertEquals("a", label1.as(Label.class).getValue());
		ComponentAgent label2 = desktop.query("#l2");
		Assertions.assertEquals("bbb", label2.as(Label.class).getValue());
		ComponentAgent div = desktop.query("#list");
		Assertions.assertEquals(6, div.getChildren().size());
		ComponentAgent textbox = desktop.query("#tb");
		textbox.type("aaaa");
		Assertions.assertEquals("aaaa", textbox.as(Textbox.class).getValue());
		Assertions.assertEquals("aaaa", label2.as(Label.class).getValue());
	}

	@Test
	public void testException() {
		try {
			connect("/test2/F100-ZK-5018-syntax-exception.zul");
		} catch (Exception e) {
			return;
		}
		fail("Should throw exception");
	}
}
