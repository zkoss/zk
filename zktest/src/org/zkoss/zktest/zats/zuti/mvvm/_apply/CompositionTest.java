/** CompositionTest.java.

	Purpose:
		
	Description:
		
	History:
		5:39:56 PM Dec 8, 2014, Created by jumperchen

Copyright (C) 2014 Potix Corporation. All Rights Reserved.
 */
package org.zkoss.zktest.zats.zuti.mvvm._apply;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.zuti.ZutiBasicTestCase;
import org.zkoss.zktest.zats.zuti.verifier.HierarchyVerifier;
import org.zkoss.zktest.zats.zuti.verifier.NullShadowRoot;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;

/**
 * @author jumperchen
 */
public class CompositionTest extends ZutiBasicTestCase {
	@Test
	public void testBasic() {
		
		DesktopAgent desktop = connect(getTestURL("basic.zul"));

		ComponentAgent hostAgent = desktop.query("#host");
		ComponentAgent next = hostAgent.getFirstChild();

		assertEquals("yellow", next.getFirstChild().as(Label.class).getSclass());
		assertEquals("1", next.getFirstChild().as(Label.class).getValue());
		
		checkVerifier(next.getOwner(), HierarchyVerifier.class);
		
		next = next.getNextSibling();
		
		assertEquals("yellow", next.getFirstChild().as(Div.class).getSclass());
		assertEquals("Foo", next.getFirstChild().getFirstChild().as(Label.class).getValue());
		
		checkVerifier(next.getOwner(), HierarchyVerifier.class);
		
	}
	@Test
	public void testResolverIssue() {
		
		DesktopAgent desktop = connect(getTestURL("resolverIssue.zul"));

		ComponentAgent hostAgent = desktop.query("#host");
		ComponentAgent next = hostAgent.getFirstChild();

		assertEquals("AAA", next.as(Label.class).getValue());

		next = next.getNextSibling();
		
		assertEquals("aaaRef", next.as(Label.class).getValue());
		
		next = next.getNextSibling();
		
		assertEquals("BBB", next.as(Label.class).getValue());
		
		next = next.getNextSibling();
		
		assertEquals("bbbRef", next.as(Label.class).getValue());
		
		checkVerifier(hostAgent.getOwner(), HierarchyVerifier.class);	
	}
	@Test
	public void testResolverIssue2() {
		
		DesktopAgent desktop = connect(getTestURL("resolverIssue2.zul"));

		ComponentAgent hostAgent = desktop.query("#host");
		ComponentAgent next = hostAgent.getFirstChild();

		assertEquals("AAA", next.getChild(0).as(Label.class).getValue());
		assertEquals("BBB", next.getChild(1).as(Label.class).getValue());
		assertEquals("CCC-ref", next.getChild(2).as(Label.class).getValue());
		assertEquals("DDD", next.getChild(3).as(Label.class).getValue());

		next = next.getNextSibling();

		assertEquals("AAA", next.getChild(0).as(Label.class).getValue());
		assertEquals("BBB", next.getChild(1).as(Label.class).getValue());
		assertEquals("", next.getChild(2).as(Label.class).getValue());
		assertEquals("", next.getChild(3).as(Label.class).getValue());
		
		next = next.getNextSibling();

		assertEquals("AAA", next.getChild(0).as(Label.class).getValue());
		assertEquals("BBB2", next.getChild(1).as(Label.class).getValue());
		assertEquals("", next.getChild(2).as(Label.class).getValue());
		assertEquals("", next.getChild(3).as(Label.class).getValue());
		
		next = next.getNextSibling();
		
		assertEquals("AAA", next.getChild(0).as(Label.class).getValue());
		assertEquals("", next.getChild(1).as(Label.class).getValue());
		assertEquals("", next.getChild(2).as(Label.class).getValue());
		assertEquals("", next.getChild(3).as(Label.class).getValue());
		
		next = next.getNextSibling();
		
		assertEquals("AAA", next.getChild(0).as(Label.class).getValue());
		assertEquals("", next.getChild(1).as(Label.class).getValue());
		assertEquals("CCC3", next.getChild(2).as(Label.class).getValue());
		assertEquals("", next.getChild(3).as(Label.class).getValue());
		
		
		checkVerifier(hostAgent.getOwner(), HierarchyVerifier.class);	
	}
	@Test
	public void testBasicVariable() {
		
		DesktopAgent desktop = connect(getTestURL("basicVariable.zul"));

		ComponentAgent hostAgent = desktop.query("#host");
		ComponentAgent next = hostAgent.getFirstChild();

		assertEquals("yellow", next.getFirstChild().as(Label.class).getSclass());
		assertEquals("1Foo", next.getFirstChild().as(Label.class).getValue());
		
		checkVerifier(next.getOwner(), HierarchyVerifier.class);
		
		next = next.getNextSibling();
		
		assertEquals("yellow", next.getFirstChild().as(Div.class).getSclass());
		assertEquals("Foo", next.getFirstChild().getFirstChild().as(Label.class).getValue());
		
		checkVerifier(next.getOwner(), HierarchyVerifier.class);
		
	}
	@Test
	public void testBasicNested() {
		
		DesktopAgent desktop = connect(getTestURL("basicNested.zul"));

		ComponentAgent hostAgent = desktop.query("#host");
		ComponentAgent next = hostAgent.getFirstChild();

		assertEquals("yellow", next.getFirstChild().as(Div.class).getSclass());
		assertEquals("1", next.getFirstChild().getFirstChild().as(Label.class).getValue());
		
		checkVerifier(next.getOwner(), HierarchyVerifier.class);
		
		next = next.getNextSibling().getFirstChild();
		
		assertEquals("Nested Shadow:", next.getFirstChild().as(Label.class).getValue().trim());
		assertEquals("yellow", next.getLastChild().as(Div.class).getSclass());
		assertEquals("Foo", next.getLastChild().getFirstChild().as(Label.class).getValue());
		
		assertEquals(2, getAllShadowSize(next.getParent()));
		checkVerifier(next.getParent().getOwner(), HierarchyVerifier.class);
		
	}
	@Test
	public void testUpdateBasicNested() {
		
		DesktopAgent desktop = connect(getTestURL("basicNested.zul"));

		ComponentAgent hostAgent = desktop.query("#host");
		ComponentAgent next = hostAgent.getFirstChild();
		hostAgent.getLastChild().getPreviousSibling().click();

		assertEquals("yellow", next.getFirstChild().as(Div.class).getSclass());
		assertEquals("1", next.getFirstChild().getFirstChild().as(Label.class).getValue());
		
		checkVerifier(next.getOwner(), HierarchyVerifier.class);
		
		next = next.getNextSibling().getFirstChild();
		
		assertEquals("Nested Shadow:", next.getFirstChild().as(Label.class).getValue().trim());
		assertEquals("green", next.getLastChild().as(Div.class).getSclass());
		assertEquals("Foo", next.getLastChild().getFirstChild().as(Label.class).getValue());
		
		assertEquals(2, getAllShadowSize(next.getParent()));
		checkVerifier(next.getParent().getOwner(), HierarchyVerifier.class);

		hostAgent.getLastChild().click();
		next = hostAgent.getFirstChild();
		
		assertEquals("green", next.getFirstChild().as(Label.class).getSclass());
		assertEquals("2", next.getFirstChild().as(Label.class).getValue());
		
		checkVerifier(next.getOwner(), HierarchyVerifier.class);
		
		next = next.getNextSibling().getFirstChild();
		
		assertEquals("Nested Shadow:", next.getFirstChild().as(Label.class).getValue().trim());
		assertEquals("green", next.getLastChild().as(Div.class).getSclass());
		assertEquals("Foo", next.getLastChild().getFirstChild().as(Label.class).getValue());
		
		assertEquals(2, getAllShadowSize(next.getParent()));
		checkVerifier(next.getParent().getOwner(), HierarchyVerifier.class);
		
	}
	@Test
	public void testMultiRoot() {
		
		DesktopAgent desktop = connect(getTestURL("multiRoot.zul"));

		ComponentAgent hostAgent = desktop.query("#host");
		ComponentAgent next = hostAgent.getFirstChild().getFirstChild();
		
		assertEquals("**Start**", next.as(Label.class).getValue().trim());
		next = next.getNextSibling();
		assertEquals("yellow", next.as(Label.class).getSclass());
		assertEquals("1", next.as(Label.class).getValue());
		next = next.getNextSibling();
		assertEquals("**Middle**", next.as(Label.class).getValue().trim());
		next = next.getNextSibling();
		assertEquals("yellow", next.as(Label.class).getSclass());
		assertEquals("1", next.as(Label.class).getValue());
		next = next.getNextSibling();
		assertEquals("**End**", next.as(Label.class).getValue().trim());
		checkVerifier(hostAgent.getFirstChild().getOwner(), HierarchyVerifier.class);
		
		next = next.getParent().getNextSibling().getFirstChild();

		assertEquals("**Start**", next.as(Label.class).getValue().trim());
		next = next.getNextSibling();
		assertEquals("yellow", next.as(Div.class).getSclass());
		assertEquals("Foo", next.getFirstChild().as(Label.class).getValue());
		next = next.getNextSibling();
		assertEquals("**Middle**", next.as(Label.class).getValue().trim());
		next = next.getNextSibling();
		assertEquals("yellow", next.as(Div.class).getSclass());
		assertEquals("Foo", next.getFirstChild().as(Label.class).getValue());
		next = next.getNextSibling();
		assertEquals("**End**", next.as(Label.class).getValue().trim());
		
		checkVerifier(next.getParent().getOwner(), HierarchyVerifier.class);
		
	}

	@Test
	public void testUpdateMultiRoot() {
		
		DesktopAgent desktop = connect(getTestURL("multiRoot.zul"));

		ComponentAgent hostAgent = desktop.query("#host");
		hostAgent.getLastChild().click();
		
		ComponentAgent next = hostAgent.getFirstChild().getFirstChild();
		
		assertEquals("**Start**", next.as(Label.class).getValue().trim());
		next = next.getNextSibling();
		assertEquals("green", next.as(Label.class).getSclass());
		assertEquals("2", next.as(Label.class).getValue());
		next = next.getNextSibling();
		assertEquals("**Middle**", next.as(Label.class).getValue().trim());
		next = next.getNextSibling();
		assertEquals("green", next.as(Label.class).getSclass());
		assertEquals("2", next.as(Label.class).getValue());
		next = next.getNextSibling();
		assertEquals("**End**", next.as(Label.class).getValue().trim());
		checkVerifier(hostAgent.getFirstChild().getOwner(), HierarchyVerifier.class);
		
		next = next.getParent().getNextSibling().getFirstChild();

		assertEquals("**Start**", next.as(Label.class).getValue().trim());
		next = next.getNextSibling();
		assertEquals("Nested Shadow:", next.getFirstChild().as(Label.class).getValue().trim());
		next = next.getNextSibling();
		assertEquals("**Middle**", next.as(Label.class).getValue().trim());
		next = next.getNextSibling();
		assertEquals("Nested Shadow:", next.getFirstChild().as(Label.class).getValue().trim());
		next = next.getNextSibling();
		assertEquals("**End**", next.as(Label.class).getValue().trim());
		
		checkVerifier(next.getParent().getOwner(), HierarchyVerifier.class);
		
	}
	@Test
	public void testMultiNestedRoot() {
		
		DesktopAgent desktop = connect(getTestURL("multiNestedRoot.zul"));

		ComponentAgent hostAgent = desktop.query("#host");
		ComponentAgent next = hostAgent.getFirstChild().getFirstChild();
		
		assertEquals("**Start**", next.as(Label.class).getValue().trim());
		next = next.getNextSibling();
		assertEquals("yellow", next.as(Div.class).getSclass());
		assertEquals("green", next.getFirstChild().as(Label.class).getSclass());
		assertEquals("1", next.getFirstChild().as(Label.class).getValue());
		next = next.getNextSibling();
		assertEquals("red", next.as(Label.class).getSclass());
		assertEquals("1", next.as(Label.class).getValue());
		next = next.getNextSibling();
		assertEquals("**Middle**", next.as(Label.class).getValue().trim());
		next = next.getNextSibling();
		assertEquals("yellow", next.as(Div.class).getSclass());
		assertEquals("green", next.getFirstChild().as(Label.class).getSclass());
		assertEquals("1", next.getFirstChild().as(Label.class).getValue());
		next = next.getNextSibling();
		assertEquals("**End**", next.as(Label.class).getValue().trim());
		checkVerifier(hostAgent.getFirstChild().getOwner(), HierarchyVerifier.class);
		assertEquals(2, getAllShadowSize(hostAgent.getFirstChild()));
		
		next = next.getParent().getNextSibling().getFirstChild();

		assertEquals("**Start**", next.as(Label.class).getValue().trim());
		next = next.getNextSibling().getFirstChild();
		assertEquals("Nested Shadow:", next.as(Label.class).getValue().trim());
		next = next.getNextSibling();
		assertEquals("yellow", next.as(Div.class).getSclass());
		assertEquals("Foo", next.getFirstChild().as(Label.class).getValue());
		next = next.getParent().getNextSibling();
		
		assertEquals("**Middle**", next.as(Label.class).getValue().trim());
		next = next.getNextSibling().getFirstChild();
		assertEquals("Nested Shadow:", next.as(Label.class).getValue().trim());
		next = next.getNextSibling();
		assertEquals("yellow", next.as(Div.class).getSclass());
		assertEquals("Foo", next.getFirstChild().as(Label.class).getValue());
		next = next.getParent().getNextSibling();
		next = next.getFirstChild();
		assertEquals("Nested Shadow:", next.as(Label.class).getValue().trim());
		next = next.getNextSibling();
		assertEquals("yellow", next.as(Div.class).getSclass());
		assertEquals("Foo", next.getFirstChild().as(Label.class).getValue());
		next = next.getParent().getParent();
		
		checkVerifier(next.getOwner(), HierarchyVerifier.class);
		assertEquals(3, getShadowSize(next));
		assertEquals(6, getAllShadowSize(next));
	}

	@Test
	public void testUpdateMultiNestedRoot() {
		
		DesktopAgent desktop = connect(getTestURL("multiNestedRoot.zul"));

		ComponentAgent hostAgent = desktop.query("#host");
		hostAgent.getLastChild().click();
		ComponentAgent next = hostAgent.getFirstChild().getFirstChild();
		
		assertEquals("**Start**", next.as(Label.class).getValue().trim());
		next = next.getNextSibling();
		assertEquals("green", next.as(Div.class).getSclass());
		assertEquals("green", next.getFirstChild().as(Label.class).getSclass());
		assertEquals("1", next.getFirstChild().as(Label.class).getValue());
		next = next.getNextSibling();
		assertEquals("yellow", next.as(Label.class).getSclass());
		assertEquals("1", next.as(Label.class).getValue());
		next = next.getNextSibling();
		assertEquals("**Middle**", next.as(Label.class).getValue().trim());
		next = next.getNextSibling();
		assertEquals("green", next.as(Div.class).getSclass());
		assertEquals("green", next.getFirstChild().as(Label.class).getSclass());
		assertEquals("1", next.getFirstChild().as(Label.class).getValue());
		next = next.getNextSibling();
		assertEquals("**End**", next.as(Label.class).getValue().trim());
		checkVerifier(hostAgent.getFirstChild().getOwner(), HierarchyVerifier.class);
		assertEquals(2, getAllShadowSize(hostAgent.getFirstChild()));
		
		next = next.getParent().getNextSibling().getFirstChild();

		assertEquals("**Start**", next.as(Label.class).getValue().trim());
		next = next.getNextSibling().getFirstChild();
		assertEquals("Nested Shadow:", next.as(Label.class).getValue().trim());
		next = next.getNextSibling();
		assertEquals("green", next.as(Div.class).getSclass());
		assertEquals("Foo", next.getFirstChild().as(Label.class).getValue());
		next = next.getParent().getNextSibling();
		
		assertEquals("**Middle**", next.as(Label.class).getValue().trim());
		next = next.getNextSibling().getFirstChild();
		assertEquals("Nested Shadow:", next.as(Label.class).getValue().trim());
		next = next.getNextSibling();
		assertEquals("green", next.as(Div.class).getSclass());
		assertEquals("Foo", next.getFirstChild().as(Label.class).getValue());
		next = next.getParent().getNextSibling();
		next = next.getFirstChild();
		assertEquals("Nested Shadow:", next.as(Label.class).getValue().trim());
		next = next.getNextSibling();
		assertEquals("green", next.as(Div.class).getSclass());
		assertEquals("Foo", next.getFirstChild().as(Label.class).getValue());
		next = next.getParent().getParent();
		
		checkVerifier(next.getOwner(), HierarchyVerifier.class);
		assertEquals(3, getShadowSize(next));
		assertEquals(6, getAllShadowSize(next));
	}
	@Test
	public void testBasicMerge() {
		
		DesktopAgent desktop = connect(getTestURL("basicMerge.zul"));

		ComponentAgent hostAgent = desktop.query("#host");
		ComponentAgent next = hostAgent.getFirstChild();

		assertEquals("yellow", next.getFirstChild().as(Label.class).getSclass());
		assertEquals("1", next.getFirstChild().as(Label.class).getValue());
		assertEquals(1, getAllShadowSize(next));
		checkVerifier(next.getOwner(), HierarchyVerifier.class);
		
		next = next.getNextSibling();
		
		assertEquals("yellow", next.getFirstChild().as(Div.class).getSclass());
		assertEquals("Foo", next.getFirstChild().getFirstChild().as(Label.class).getValue());
		
		checkVerifier(next.getOwner(), NullShadowRoot.class);
		
	}

	@Test
	public void testNestedMerge() {
		
		DesktopAgent desktop = connect(getTestURL("basicNestedMerge.zul"));

		ComponentAgent hostAgent = desktop.query("#host");
		ComponentAgent next = hostAgent.getFirstChild();
		next = next.getFirstChild();
		assertEquals("yellow", next.as(Div.class).getSclass());
		assertEquals("1", next.getFirstChild().as(Label.class).getValue());
		assertEquals(0, getAllShadowSize(next));
		checkVerifier(next.getOwner(), NullShadowRoot.class);
		
		next = next.getParent();
		assertEquals(1, getAllShadowSize(next));
		checkVerifier(next.getOwner(), HierarchyVerifier.class);
		
		next = next.getNextSibling().getFirstChild().getLastChild();
		
		assertEquals("yellow", next.as(Div.class).getSclass());
		assertEquals("Foo", next.getFirstChild().as(Label.class).getValue());

		next = next.getParent();
		assertEquals(0, getAllShadowSize(next));
		checkVerifier(next.getOwner(), NullShadowRoot.class);
		
		next = next.getParent();
		assertEquals(1, getAllShadowSize(next));
		checkVerifier(next.getOwner(), HierarchyVerifier.class);
		
	}
	@Test
	public void testUpdateBasic() {
		
		DesktopAgent desktop = connect(getTestURL("basic.zul"));

		ComponentAgent hostAgent = desktop.query("#host");
		ComponentAgent next = hostAgent.getFirstChild();
		hostAgent.getLastChild().click();

		assertEquals("green", next.getFirstChild().as(Label.class).getSclass());
		assertEquals("2", next.getFirstChild().as(Label.class).getValue());
		assertEquals(1, getAllShadowSize(next));
		checkVerifier(next.getOwner(), HierarchyVerifier.class);
		
		next = next.getNextSibling();
		
		assertEquals("Nested Shadow:", next.getFirstChild().getFirstChild().as(Label.class).getValue().trim());
		assertEquals(1, next.getFirstChild().getChildren().size());

		checkVerifier(next.getOwner(), HierarchyVerifier.class);
		
	}
}
