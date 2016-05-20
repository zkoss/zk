/** NestedApplyTest.java.

	Purpose:
		
	Description:
		
	History:
		3:11:34 PM Jan 26, 2015, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.zuti.complex;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zhtml.Text;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Label;
import org.zkoss.zul.Panel;
import org.zkoss.zul.Vlayout;
import org.zkoss.zul.Window;
import org.zkoss.zktest.zats.zuti.ZutiBasicTestCase;
import org.zkoss.zktest.zats.zuti.verifier.HierarchyVerifier;

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
