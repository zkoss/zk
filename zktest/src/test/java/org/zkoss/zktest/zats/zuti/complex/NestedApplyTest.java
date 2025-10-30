/** NestedApplyTest.java.

	Purpose:
		
	Description:
		
	History:
		3:11:34 PM Jan 26, 2015, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.zuti.complex;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.zuti.ZutiBasicTestCase;
import org.zkoss.zul.Label;

/**
 * @author jumperchen
 *
 */
public class NestedApplyTest extends ZutiBasicTestCase {
	@SuppressWarnings("unchecked")
	@Test
	public void testResult() {
		
		DesktopAgent desktop = connect();
		List<ComponentAgent> rows = desktop.queryAll("row");
		Iterator<ComponentAgent> itRows = rows.iterator();
		List<String> list = new ArrayList<String>();
        for (int i = 0; i < 100; i++) {
            list.add("item" + i);
        }
		for (String loc : list) {
			assertEquals(loc, itRows.next().getFirstChild().as(Label.class).getValue().trim());
		}
		assertEquals(0, getAllShadowSize(desktop.query("#host")));
	}
}
