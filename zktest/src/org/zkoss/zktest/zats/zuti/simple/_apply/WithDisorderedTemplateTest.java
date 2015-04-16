/** WithDisorderedTemplateTest.java.

	Purpose:
		
	Description:
		
	History:
		5:35:03 PM Feb 12, 2015, Created by jameschu

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
 */
package org.zkoss.zktest.zats.zuti.simple._apply;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.zuti.ZutiBasicTestCase;
import org.zkoss.zktest.zats.zuti.verifier.NullShadowRoot;
import org.zkoss.zul.Label;

/**
 * @author jameschu
 */
public class WithDisorderedTemplateTest extends ZutiBasicTestCase {
	@Test
	public void testResult() {
		DesktopAgent desktop = connect();

		List<ComponentAgent> queryAll = desktop.queryAll("div");
		assertEquals("test with a name", queryAll.get(0).getFirstChild().as(Label.class).getValue());
		
		List<ComponentAgent> queryAll2 = desktop.queryAll("div");
		for (ComponentAgent hostAgent : queryAll2)
			checkVerifier(hostAgent.getOwner(), NullShadowRoot.class);
	}
}
