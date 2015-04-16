/** CompositionTest.java.

	Purpose:
		
	Description:
		
	History:
		3:04:13 PM Nov 27, 2014, Created by jumperchen

Copyright (C) 2014 Potix Corporation. All Rights Reserved.
 */
package org.zkoss.zktest.zats.zuti.simple._if;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.zuti.ZutiBasicTestCase;
import org.zkoss.zktest.zats.zuti.verifier.NullShadowRoot;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;

/**
 * @author jumperchen
 */
public class CompositionTest extends ZutiBasicTestCase {

	@Test
	public void testBasicNested() {
		DesktopAgent desktop = connect(getTestURL("nestedIf.zul"));

		ComponentAgent hostAgent = desktop.query("#host");
		ComponentAgent next = hostAgent.getFirstChild();
		assertEquals(3, next.getChildren().size());

		assertEquals("1. First", next.getChild(0).as(Label.class).getValue()
				.trim());
		assertEquals("Without Template",
				next.getChild(1).getFirstChild().as(Label.class).getValue()
						.trim());
		assertEquals("3. Last", next.getChild(2).as(Label.class).getValue()
				.trim());

		checkVerifier(hostAgent.getOwner(), NullShadowRoot.class);

		hostAgent = hostAgent.getNextSibling();
		next = hostAgent.getFirstChild();

		assertEquals(3, next.getChildren().size());

		assertEquals("1. First", next.getChild(0).as(Label.class).getValue()
				.trim());
		assertEquals("With Template",
				next.getChild(1).getFirstChild().as(Label.class).getValue()
						.trim());
		assertEquals("3. Last", next.getChild(2).as(Label.class).getValue()
				.trim());

		checkVerifier(hostAgent.getOwner(), NullShadowRoot.class);
	}
	
	@Test
	public void testMultiRoot() {
		DesktopAgent desktop = connect(getTestURL("multiRoot.zul"));

		ComponentAgent hostAgent = desktop.query("#host");
		assertEquals(5, hostAgent.getChildren().size());
		
		ComponentAgent next = hostAgent.getFirstChild();
		assertEquals("**Start**", next.as(Label.class).getValue().trim());

		next = next.getNextSibling();
		assertEquals("background:yellow", next.as(Div.class).getStyle());
		assertEquals("Without Template", next.getFirstChild().as(Label.class).getValue().trim());

		next = next.getNextSibling();
		assertEquals("**Middle**", next.as(Label.class).getValue().trim());
		
		next = next.getNextSibling();
		assertEquals("background:yellow", next.as(Div.class).getStyle());
		assertEquals("Without Template", next.getFirstChild().as(Label.class).getValue().trim());
		
		next = next.getNextSibling();
		assertEquals("**End**", next.as(Label.class).getValue().trim());
	}
	
	@Test
	public void testMultiNestedIf() {
		DesktopAgent desktop = connect(getTestURL("multiNestedIf.zul"));

		ComponentAgent hostAgent = desktop.query("#host");
		assertEquals(5, hostAgent.getChildren().size());
		ComponentAgent next = hostAgent.getFirstChild();
		assertEquals("**Start**", next.as(Label.class).getValue().trim());

		next = next.getNextSibling();
		assertEquals("1. First", next.getChild(0).as(Label.class).getValue()
				.trim());
		assertEquals("Without Template",
				next.getChild(1).getFirstChild().as(Label.class).getValue()
						.trim());
		assertEquals("3. Last", next.getChild(2).as(Label.class).getValue()
				.trim());

		next = next.getNextSibling();
		assertEquals("**Middle**", next.as(Label.class).getValue().trim());
		
		next = next.getNextSibling();
		assertEquals("1. First", next.getChild(0).as(Label.class).getValue()
				.trim());
		assertEquals("With Template",
				next.getChild(1).getFirstChild().as(Label.class).getValue()
						.trim());
		assertEquals("3. Last", next.getChild(2).as(Label.class).getValue()
				.trim());
		
		next = next.getNextSibling();
		assertEquals("**End**", next.as(Label.class).getValue().trim());
		
		checkVerifier(hostAgent.getOwner(), NullShadowRoot.class);
	}
	
}
