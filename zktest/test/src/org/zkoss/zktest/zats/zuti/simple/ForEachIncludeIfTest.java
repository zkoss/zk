/** ForEachIncludeIfTest.java.

	Purpose:
		
	Description:
		
	History:
		10:45:23 AM Nov 13, 2014, Created by jumperchen

Copyright (C) 2014 Potix Corporation. All Rights Reserved.
 */
package org.zkoss.zktest.zats.zuti.simple;

import static org.junit.Assert.*;

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
public class ForEachIncludeIfTest extends ZutiBasicTestCase {
	@Test
	public void testResult() {
		DesktopAgent desktop = connect();

		ComponentAgent hostAgent = desktop.query("#host");
		assertTrue(hostAgent.getChildren().size() == 4);
		int index = -1;
		for (ComponentAgent cmp : hostAgent.getChildren()) {
			if (++index == 2) {
				assertEquals("color:blue", cmp.as(Div.class).getStyle());
				assertEquals("Only 2 Template", cmp.getChild(0).as(Label.class).getValue());
			} else {
				assertEquals("color:red", cmp.as(Div.class).getStyle());
				assertEquals(index + " Template", cmp.getChild(0).as(Label.class).getValue());
			}
		}
		checkVerifier(hostAgent.getOwner(), NullShadowRoot.class);
	}
}
