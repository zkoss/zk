/** NestedIfTest.java.

	Purpose:
		
	Description:
		
	History:
		10:04:08 AM Nov 26, 2014, Created by jumperchen

Copyright (C) 2014 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.zuti.mvvm._if;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zktest.zats.zuti.ZutiBasicTestCase;
import org.zkoss.zktest.zats.zuti.verifier.HierarchyVerifier;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;

/**
 * @author jumperchen
 *
 */
public class NestedIfTest extends ZutiBasicTestCase {
	@Test
	public void testResult() {
		DesktopAgent desktop = connect();

		ComponentAgent hostAgent = desktop.query("#host");
		AbstractComponent host = hostAgent.as(Div.class);
		List<AbstractComponent> children = host.getChildren();
		assertEquals(7, children.size());
		assertEquals(5, getShadowSize(hostAgent));
		
		for (AbstractComponent cmp : children)
			assertEquals(cmp.toString(), ((Label)cmp.getFirstChild()).getValue());
		
		checkVerifier(host, HierarchyVerifier.class);
	}
	@Test
	public void testUpdate() {
		DesktopAgent desktop = connect();

		ComponentAgent hostAgent = desktop.query("#host");
		AbstractComponent host = hostAgent.as(Div.class);
		List<AbstractComponent> children = host.getChildren();
		assertEquals(7, children.size());
		assertEquals(5, getShadowSize(hostAgent));
		
		hostAgent.getNextSibling().click();

		children = host.getChildren();
		assertEquals(1, children.size());
		assertEquals(3, getShadowSize(hostAgent));
		for (AbstractComponent cmp : children)
			assertEquals(cmp.toString(), ((Label)cmp.getFirstChild()).getValue());
		
		checkVerifier(host, HierarchyVerifier.class);
	}
}
