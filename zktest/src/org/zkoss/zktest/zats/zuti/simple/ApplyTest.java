/** ApplyTest.java.

	Purpose:
		
	Description:
		
	History:
		3:41:54 PM Nov 13, 2014, Created by jumperchen

Copyright (C) 2014 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.zuti.simple;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Label;
import org.zkoss.zktest.zats.zuti.ZutiBasicTestCase;
import org.zkoss.zktest.zats.zuti.verifier.NullShadowRoot;

/**
 * @author jumperchen
 *
 */
public class ApplyTest extends ZutiBasicTestCase {

	@Test
	@SuppressWarnings("unchecked")
	public void testResult() {
		DesktopAgent desktop = connect();
		ComponentAgent hostAgent = desktop.query("#host");
		assertTrue(hostAgent.getChildren().size() == 8);
		List<ComponentAgent> children = hostAgent.getChildren();
		for (Iterator<ComponentAgent> it = children.iterator(); it.hasNext();) {
			ComponentAgent label = it.next();
			ComponentAgent result = it.next();
			assertEquals(label.as(Label.class).getValue().trim(), result.getFirstChild().as(Label.class).getValue().trim() + ':');
		}
		checkVerifier(hostAgent.getOwner(), NullShadowRoot.class);
	}
}
