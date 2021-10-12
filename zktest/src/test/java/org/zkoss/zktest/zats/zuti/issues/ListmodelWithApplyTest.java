/** ListmodelWithApplyTest.java.

	Purpose:
		
	Description:
		
	History:
		9:45:06 AM Jan 29, 2015, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.zuti.issues;

import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.zuti.ZutiBasicTestCase;
import org.zkoss.zul.Listitem;

/**
 * @author jumperchen
 *
 */
public class ListmodelWithApplyTest extends ZutiBasicTestCase {
	@Test
	public void testResult() {
		
		try {
			DesktopAgent desktop = connect();
			List<ComponentAgent> items = desktop.queryAll("listitem");
			Iterator<ComponentAgent> it = items.iterator();
			for (String content : new String[] {"aaa", "bbb", "ccc"}) {
				assertEquals(content, it.next().as(Listitem.class).getLabel());
			}
			assertTrue(items.get(1).as(Listitem.class).isSelected());
		} catch (Exception e) {
			fail();
		}
	}
}
