/** NamespaceTest.java.

	Purpose:
		
	Description:
		
	History:
		10:45:23 AM Jan 28, 2015, Created by jameschu

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
 */
package org.zkoss.zktest.zats.zuti.simple;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zul.Label;
import org.zkoss.zktest.zats.zuti.ZutiBasicTestCase;
import org.zkoss.zktest.zats.zuti.verifier.NullShadowRoot;

/**
 * @author jameschu
 */
public class NamespaceTest  extends ZutiBasicTestCase {
	@Test
	public void testResult() {
		DesktopAgent desktop = connect();

		List<ComponentAgent> queryAll = desktop.queryAll("div > div > label");
		assertTrue(queryAll.size() == 2);
		assertEquals("try custom namespace(name)", queryAll.get(0).as(Label.class).getValue());
		assertEquals("try custom namespace(namespace)", queryAll.get(1).as(Label.class).getValue());
		
		List<ComponentAgent> queryAll2 = desktop.queryAll("div");
		for (ComponentAgent hostAgent : queryAll2)
			checkVerifier(hostAgent.getOwner(), NullShadowRoot.class);
		
	}

}
