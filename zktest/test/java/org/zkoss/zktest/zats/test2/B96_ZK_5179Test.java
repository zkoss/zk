/* B96_ZK_5179Test.java

	Purpose:
		
	Description:
		
	History:
		Mon Sep 26 17:10:22 CST 2022, Created by jameschu

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

/**
 * @author jameschu
 */
public class B96_ZK_5179Test extends ZATSTestCase {
	@Test
	public void test() throws Exception {
		DesktopAgent desktop = connect();
		ComponentAgent anchor1 = desktop.query("#anchor1");
		ComponentAgent anchor2 = desktop.query("#anchor2");
		ComponentAgent anchor3 = desktop.query("#anchor3");
		ComponentAgent next1 = anchor1.getNextSibling().getFirstChild().getNextSibling(); // vlayout -> label
		ComponentAgent next2 = anchor2.getNextSibling().getNextSibling(); // label
		ComponentAgent next3 = anchor3.getNextSibling().getNextSibling(); // label
		String value = next1.as(Label.class).getValue().trim();
		assertEquals(value, next2.as(Label.class).getValue().trim());
		assertEquals(value, next3.as(Label.class).getValue().trim());
		next1 = next1.getNextSibling().getNextSibling();
		next2 = next2.getNextSibling().getNextSibling();
		next3 = next3.getNextSibling().getNextSibling();
		value = next1.as(Label.class).getValue().trim();
		assertEquals(value, next2.as(Label.class).getValue().trim());
		assertEquals(value, next3.as(Label.class).getValue().trim());
		next1 = next1.getNextSibling().getNextSibling();
		next2 = next2.getNextSibling().getNextSibling();
		next3 = next3.getNextSibling().getNextSibling();
		value = next1.as(Label.class).getValue().trim();
		assertEquals(value, next2.as(Label.class).getValue().trim());
		assertEquals(value, next3.as(Label.class).getValue().trim());
	}
}