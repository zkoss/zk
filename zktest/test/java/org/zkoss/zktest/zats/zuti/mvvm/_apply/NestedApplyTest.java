/** NestedApplyTest.java.

	Purpose:
		
	Description:
		
	History:
		9:50:45 AM Nov 25, 2014, Created by jumperchen

Copyright (C) 2014 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.zuti.mvvm._apply;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.zuti.ZutiBasicTestCase;
import org.zkoss.zktest.zats.zuti.verifier.HierarchyVerifier;
import org.zkoss.zul.Label;
import org.zkoss.zul.Separator;

/**
 * @author jumperchen
 *
 */
public class NestedApplyTest extends ZutiBasicTestCase {
	
	@Test
	public void testResult() {
		DesktopAgent desktop = connect();
		ComponentAgent hostAgent = desktop.query("#host");
		assertTrue(hostAgent.getChildren().size() == 6);
		assertTrue(getShadowSize(hostAgent) == 5);
		
		List<ComponentAgent> children = hostAgent.getChildren();

		int index = 0;
		assertEquals(children.get(index++).as(Label.class).getValue(), "1");
		assertTrue(children.get(index++).getOwner() instanceof Separator);
		assertEquals(children.get(index++).as(Label.class).getValue(), "Second");
		assertTrue(children.get(index++).getOwner() instanceof Separator);
		assertEquals(children.get(index++).as(Label.class).getValue(), "Nested Third");
		assertTrue(children.get(index++).getOwner() instanceof Separator);
		
		
		checkVerifier(hostAgent.getOwner(), HierarchyVerifier.class);
	}
}
