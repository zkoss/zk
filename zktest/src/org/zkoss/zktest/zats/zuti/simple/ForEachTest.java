/** ForEachTest.java.

	Purpose:
		
	Description:
		
	History:
		5:35:03 PM Nov 5, 2014, Created by jumperchen

Copyright (C) 2014 Potix Corporation. All Rights Reserved.
 */
package org.zkoss.zktest.zats.zuti.simple;

import static org.junit.Assert.*;

import java.util.List;
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
 */
public class ForEachTest extends ZutiBasicTestCase {
	@Test
	public void testResult() {
		DesktopAgent desktop = connect();

		ComponentAgent hostAgent = desktop.query("#host");
		checkChildren(hostAgent, new Integer[]{0,1,2,3},  1);
		hostAgent = hostAgent.getNextSibling();
		checkChildren(hostAgent, new Integer[]{0,1,2,3},  2);
		hostAgent = hostAgent.getNextSibling();
		checkChildren(hostAgent, new Object[]{"one", "two", "three"},  1);
		hostAgent = hostAgent.getNextSibling();
		checkChildren(hostAgent, new Object[]{"two", "three"},  1);
	}
	private void checkChildren(ComponentAgent parent, Object[] data, int step) {
		List<ComponentAgent> children = parent.getChildren();
		assertTrue(children.size() == data.length / step + 1);
		int index = 0;
		for (ListIterator<ComponentAgent> it = parent.getChildren().listIterator(1); it.hasNext(); index += step) {
			ComponentAgent cmp = it.next();
			assertEquals("color:blue", cmp.as(Div.class).getStyle());
			assertEquals(data[index] + " Template", cmp.getChild(0).as(Label.class).getValue());;
		}

		checkVerifier(parent.getOwner(), NullShadowRoot.class);
	}
}
