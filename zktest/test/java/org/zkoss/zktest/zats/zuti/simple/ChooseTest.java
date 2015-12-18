/** ChooseTest.java.

	Purpose:
		
	Description:
		
	History:
		2:55:27 PM Nov 19, 2014, Created by jumperchen

Copyright (C) 2014 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.zuti.simple;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ListIterator;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zktest.zats.zuti.ZutiBasicTestCase;
import org.zkoss.zktest.zats.zuti.verifier.NullShadowRoot;

/**
 * @author jumperchen
 *
 */
public class ChooseTest extends ZutiBasicTestCase {
	
	@SuppressWarnings("unchecked")
	@Test
	public void testResult() {
		DesktopAgent desktop = connect();

		ComponentAgent hostAgent = desktop.query("#host");
		assertTrue(hostAgent.getChildren().size() == 5);
		int index = -1;
		for (ListIterator<ComponentAgent> it = hostAgent.getChildren()
				.listIterator(1); it.hasNext();) {
			ComponentAgent next = it.next();
			if (++index == 0) {
				assertEquals("color:blue", next.as(Div.class).getStyle());
				
			} else if (index == 1) {
				assertEquals("color:yellow", next.as(Div.class).getStyle());
			} else {
				assertEquals("color:red", next.as(Div.class).getStyle());
			}
			assertEquals(index + " Template", next.getChild(0).as(Label.class).getValue());
		}
		checkVerifier(hostAgent.getOwner(), NullShadowRoot.class);
	}
}
