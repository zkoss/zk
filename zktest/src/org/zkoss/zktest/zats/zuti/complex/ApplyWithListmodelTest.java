/** ApplyWithListmodelTest.java.

	Purpose:
		
	Description:
		
	History:
		4:27:24 PM Jan 27, 2015, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.zuti.complex;

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
public class ApplyWithListmodelTest extends ZutiBasicTestCase {
	@Test
	public void testResult() {
		try {
			DesktopAgent desktop = connect();
			List<ComponentAgent> items = desktop.queryAll("listitem");
			Iterator<ComponentAgent> it = items.iterator();
			for (String content : new String[] {"aaa", "bbb", "ccc"}) {
				assertEquals(content, it.next().as(Listitem.class).getLabel());
			}
			ComponentAgent button = desktop.query("button");
			button.click();
			items = desktop.queryAll("listitem");
			it = items.iterator();
			for (String content : new String[] {"ccc", "ddd", "eee", "fff"}) {
				assertEquals(content, it.next().as(Listitem.class).getLabel());
			}
			button.click();
		} catch (Exception e) {
			fail();
		}
	}
}
