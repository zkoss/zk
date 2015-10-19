/* B80_ZK_2925Test.java

	Purpose:
		
	Description:
		
	History:
		5:06 PM 10/19/15, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

/**
 * @author jumperchen
 */
public class B80_ZK_2925Test extends ZATSTestCase {

	@Test
	public void test() {
		try {
			DesktopAgent desktopAgent = connect();
			List<ComponentAgent> buttons = desktopAgent.queryAll("button");

			ComponentAgent owner = desktopAgent.query("#owner");
			List<ComponentAgent> children = owner.getFirstChild().getChildren();

			assertEquals(4, children.size());

			assertEquals("aaa", children.get(0).as(Label.class).getValue());
			assertEquals("bbb", children.get(1).as(Label.class).getValue());
			assertEquals("ccc", children.get(2).as(Label.class).getValue());
			assertEquals("ddd", children.get(3).as(Label.class).getValue());

			buttons.get(0).click();

			children = owner.getFirstChild().getChildren();
			assertEquals(0, children.size());

			buttons.get(1).click();

			children = owner.getFirstChild().getChildren();
			assertEquals(1, children.size());

			assertEquals("new item", children.get(0).as(Label.class).getValue());

			assertTrue(true);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
}
