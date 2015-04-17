/** OrderingIssue3Test.java.

	Purpose:
		
	Description:
		
	History:
		14:39:01 AM Apr 17, 2015, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
 */
package org.zkoss.zktest.zats.zuti.mvvm;

import static org.junit.Assert.*;

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
public class OrderingIssue3Test extends ZutiBasicTestCase {
	@Test
	public void testResult() {

		DesktopAgent desktop = connect();

		ComponentAgent host = desktop.query("#host");
		assertEquals(1, host.getChildren().size());
		ComponentAgent next = host.getFirstChild();
		assertEquals(1, next.getChildren().size());
		next = next.getFirstChild();
		assertEquals(3, next.getChildren().size());

		assertEquals("Shipping:", next.getChild(1).getFirstChild()
				.as(Label.class).getValue().trim());

		assertTrue(next.getLastChild().as(Button.class) instanceof Button);
		
		assertEquals(4, getAllShadowSize(host));
		
		checkVerifier(host.getOwner(), HierarchyVerifier.class);
		
		try {
			next.getChild(1).getLastChild().getFirstChild().type("aaaaaaaa");
			next.getLastChild().click();
		} catch (Exception e) {
			fail("Should not run this line!");
		}
		assertEquals(4, next.getChildren().size());
		assertEquals("Shipping:", next.getChild(1).getFirstChild().getFirstChild()
				.as(Label.class).getValue().trim());
		assertEquals("aaaaaaaa", next.getChild(1).getFirstChild().getLastChild()
				.as(Label.class).getValue().trim());
		
		assertTrue(next.getLastChild().as(Button.class) instanceof Button);

		assertTrue(next.getLastChild().getPreviousSibling().as(Button.class) instanceof Button);

		assertEquals(4, getAllShadowSize(host));
		
		checkVerifier(next.getOwner(), HierarchyVerifier.class);
		

		next.getChild(1).getLastChild().getFirstChild().type("bbbbbbbb");
		next.getLastChild().click();

		assertTrue(next.getLastChild().as(Button.class) instanceof Button);

		assertTrue(next.getLastChild().getPreviousSibling().as(Button.class) instanceof Button);

		assertEquals(4, getAllShadowSize(host));
		
		checkVerifier(next.getOwner(), HierarchyVerifier.class);
	}

}
