/** OrderingIssueTest.java.

	Purpose:
		
	Description:
		
	History:
		9:39:01 AM Apr 16, 2015, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
 */
package org.zkoss.zktest.zats.zuti.mvvm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zktest.zats.zuti.ZutiBasicTestCase;
import org.zkoss.zktest.zats.zuti.verifier.HierarchyVerifier;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Window;

/**
 * @author jumperchen
 */
public class OrderingIssueTest extends ZutiBasicTestCase {
	@Test
	public void testResult() {

		DesktopAgent desktop = connect();

		ComponentAgent host = desktop.query("#host");
		assertEquals(3, host.getChildren().size());

		AbstractComponent hostCmp = host.as(Window.class);

		assertEquals("Shipping:", host.getChild(1).getFirstChild()
				.as(Label.class).getValue().trim());

		assertTrue(host.getLastChild().as(Button.class) instanceof Button);
		
		assertEquals(4, getShadowSize(host));
		
		checkVerifier(hostCmp, HierarchyVerifier.class);
		
		host.getChild(1).getLastChild().getFirstChild().type("aaaaaaaa");
		host.getLastChild().click();
		
		assertTrue(host.getChildren().size() == 4);
		assertEquals("Shipping:", host.getChild(1).getFirstChild().getFirstChild()
				.as(Label.class).getValue().trim());
		assertEquals("aaaaaaaa", host.getChild(1).getFirstChild().getLastChild()
				.as(Label.class).getValue().trim());
		

		assertTrue(host.getLastChild().as(Button.class) instanceof Button);

		assertTrue(host.getLastChild().getPreviousSibling().as(Button.class) instanceof Button);
		

		assertEquals(4, getShadowSize(host));
		
		checkVerifier(hostCmp, HierarchyVerifier.class);
	}

}
