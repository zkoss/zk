/** IfTest.java.

	Purpose:
		
	Description:
		
	History:
		5:35:03 PM Nov 5, 2014, Created by jumperchen

Copyright (C) 2014 Potix Corporation. All Rights Reserved.
 */
package org.zkoss.zktest.zats.zuti.simple;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zul.Label;
import org.zkoss.zktest.zats.zuti.ZutiBasicTestCase;
import org.zkoss.zktest.zats.zuti.verifier.NullShadowRoot;

/**
 * @author jumperchen
 */
public class IfTest extends ZutiBasicTestCase {
	@Test
	public void testResult() {
		DesktopAgent desktop = connect();

		List<ComponentAgent> queryAll = desktop.queryAll("div > div > label");
		assertTrue(queryAll.size() == 2);
		assertEquals("Without Template", queryAll.get(0).as(Label.class).getValue());
		assertEquals("With Template", queryAll.get(1).as(Label.class).getValue());
		
		List<ComponentAgent> queryAll2 = desktop.queryAll("div");
		for (ComponentAgent hostAgent : queryAll2)
			checkVerifier(hostAgent.getOwner(), NullShadowRoot.class);
	}
}
