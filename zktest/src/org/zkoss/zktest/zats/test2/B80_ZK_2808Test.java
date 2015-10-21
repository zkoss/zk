/* B80_ZK_2808Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Oct 21 11:01:21 CST 2015, Created by chunfu

Copyright (C)  2015 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

/**
 * 
 * @author chunfu
 */
public class B80_ZK_2808Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		List<ComponentAgent> labels = desktop.queryAll("label");
		assertEquals("true", labels.get(1).as(Label.class).getValue());
		assertEquals("3.14", labels.get(2).as(Label.class).getValue());
		assertEquals("true", labels.get(3).as(Label.class).getValue());

		for (int i = 1; i < 4; i++) {
			assertEquals(labels.get(i).as(Label.class).getValue(), labels.get(i + 3).as(Label.class).getValue());
		}
	}
}
