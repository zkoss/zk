/** CompositionTest.java.

	Purpose:
		
	Description:
		
	History:
		4:18:20 PM Dec 3, 2014, Created by jumperchen

Copyright (C) 2014 Potix Corporation. All Rights Reserved.
 */
package org.zkoss.zktest.zats.zuti.mvvm._if;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.zuti.ZutiBasicTestCase;
import org.zkoss.zktest.zats.zuti.verifier.HierarchyVerifier;
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

		assertEquals(2, getShadowSize(hostAgent));
		assertEquals(2, hostAgent.getChildren().size());

		ComponentAgent next = hostAgent.getFirstChild();

		assertEquals("background:yellow", next.as(Div.class).getStyle());

		assertEquals(next.getOwner().toString(),
				next.getFirstChild().as(Label.class).getValue().trim());

		next = next.getNextSibling();

		assertEquals("background:green", next.as(Div.class).getStyle());

		assertEquals(next.getOwner().toString(),
				next.getFirstChild().as(Label.class).getValue().trim());

		checkVerifier(hostAgent.getOwner(), HierarchyVerifier.class);
	}

	@Test
	public void testNestedHost() {
		DesktopAgent desktop = connect(getTestURL("nestedHost.zul"));

		ComponentAgent hostAgent = desktop.query("#host");

		assertEquals(1, getShadowSize(hostAgent));
		assertEquals(3, hostAgent.getChildren().size());

		ComponentAgent next = hostAgent.getFirstChild();

		assertEquals("1. First", next.as(Label.class).getValue().trim());

		next = next.getNextSibling();

		assertEquals(1, getShadowSize(next));
		assertEquals("background:yellow", next.getFirstChild().as(Div.class)
				.getStyle());

		assertEquals(next.getFirstChild().getOwner().toString(), next
				.getFirstChild().getFirstChild().as(Label.class).getValue()
				.trim());

		assertEquals("background:green", next.getLastChild().as(Div.class)
				.getStyle());

		assertEquals(next.getLastChild().getOwner().toString(), next
				.getLastChild().getFirstChild().as(Label.class).getValue()
				.trim());

		checkVerifier(next.getOwner(), HierarchyVerifier.class);
		next = next.getNextSibling();
		assertEquals("3. Last", next.as(Label.class).getValue().trim());

		checkVerifier(hostAgent.getOwner(), HierarchyVerifier.class);
	}

	@Test
	public void testUpdateNestedHost() {
		DesktopAgent desktop = connect(getTestURL("nestedHost.zul"));

		ComponentAgent hostAgent = desktop.query("#host");

		assertEquals(1, getShadowSize(hostAgent));
		assertEquals(3, hostAgent.getChildren().size());

		hostAgent.getNextSibling().click();
		
		ComponentAgent next = hostAgent.getFirstChild();
		
		assertEquals(1, getShadowSize(hostAgent));
		assertEquals(3, hostAgent.getChildren().size());

		assertEquals("1. First", next.as(Label.class).getValue().trim());

		next = next.getNextSibling();

		assertEquals(1, getShadowSize(next));
		
		assertEquals("background:green", next.getLastChild().as(Div.class)
				.getStyle());

		assertEquals(next.getLastChild().getOwner().toString(), next
				.getLastChild().getFirstChild().as(Label.class).getValue()
				.trim());

		checkVerifier(next.getOwner(), HierarchyVerifier.class);
		next = next.getNextSibling();
		assertEquals("3. Last", next.as(Label.class).getValue().trim());

		checkVerifier(hostAgent.getOwner(), HierarchyVerifier.class);
	}
	
	@Test
	public void testMultiRoot() {
		DesktopAgent desktop = connect(getTestURL("multiRoot.zul"));

		ComponentAgent hostAgent = desktop.query("#host");

		assertEquals(4, getShadowSize(hostAgent));
		assertEquals(8, hostAgent.getChildren().size());

		ComponentAgent next = hostAgent.getFirstChild();

		assertEquals("**Start**", next.as(Label.class).getValue().trim());

		next = next.getNextSibling();
		assertEquals("1. First", next.as(Label.class).getValue().trim());

		next = next.getNextSibling();
		assertEquals("background:yellow", next.as(Div.class).getStyle());
		assertEquals(next.getOwner().toString(),
				next.getFirstChild().as(Label.class).getValue().trim());

		next = next.getNextSibling();

		assertEquals("background:green", next.as(Div.class).getStyle());

		assertEquals(next.getOwner().toString(),
				next.getFirstChild().as(Label.class).getValue().trim());

		next = next.getNextSibling();
		assertEquals("**Middle**", next.as(Label.class).getValue().trim());

		next = next.getNextSibling();
		assertEquals("background:yellow", next.as(Div.class).getStyle());
		assertEquals(next.getOwner().toString(),
				next.getFirstChild().as(Label.class).getValue().trim());

		next = next.getNextSibling();

		assertEquals("background:green", next.as(Div.class).getStyle());
		assertEquals(next.getOwner().toString(),
				next.getFirstChild().as(Label.class).getValue().trim());

		next = next.getNextSibling();
		assertEquals("**End**", next.as(Label.class).getValue().trim());
		checkVerifier(hostAgent.getOwner(), HierarchyVerifier.class);
	}


	@Test
	public void testUpdateMultiRoot() {
		DesktopAgent desktop = connect(getTestURL("multiRoot.zul"));

		ComponentAgent hostAgent = desktop.query("#host");

		assertEquals(4, getShadowSize(hostAgent));
		assertEquals(8, hostAgent.getChildren().size());
		
		hostAgent.getNextSibling().click();
		assertEquals(4, getShadowSize(hostAgent));
		assertEquals(6, hostAgent.getChildren().size());

		ComponentAgent next = hostAgent.getFirstChild();

		assertEquals("**Start**", next.as(Label.class).getValue().trim());

		next = next.getNextSibling();
		assertEquals("1. First", next.as(Label.class).getValue().trim());

		next = next.getNextSibling();

		assertEquals("background:green", next.as(Div.class).getStyle());

		assertEquals(next.getOwner().toString(),
				next.getFirstChild().as(Label.class).getValue().trim());

		next = next.getNextSibling();
		assertEquals("**Middle**", next.as(Label.class).getValue().trim());

		next = next.getNextSibling();

		assertEquals("background:green", next.as(Div.class).getStyle());
		assertEquals(next.getOwner().toString(),
				next.getFirstChild().as(Label.class).getValue().trim());

		next = next.getNextSibling();
		assertEquals("**End**", next.as(Label.class).getValue().trim());
		checkVerifier(hostAgent.getOwner(), HierarchyVerifier.class);
	}

	@Test
	public void testMultiNestedHost() {
		DesktopAgent desktop = connect(getTestURL("multiNestedHost.zul"));

		ComponentAgent hostAgent = desktop.query("#host");

		assertEquals(2, getShadowSize(hostAgent));
		assertEquals(8, hostAgent.getChildren().size());

		ComponentAgent next = hostAgent.getFirstChild();

		assertEquals("**Start**", next.as(Label.class).getValue().trim());

		next = next.getNextSibling();

		assertEquals("1. First", next.as(Label.class).getValue().trim());

		next = next.getNextSibling();

		assertEquals(2, getShadowSize(next));
		assertEquals("**Nested Start**", next.getFirstChild().as(Label.class)
				.getValue().trim());
		assertEquals("background:yellow", next.getChild(1).as(Div.class)
				.getStyle());

		assertEquals(next.getChild(1).getOwner().toString(), next.getChild(1)
				.getFirstChild().as(Label.class).getValue().trim());

		assertEquals("background:green", next.getChild(2).as(Div.class)
				.getStyle());

		assertEquals(next.getChild(2).getOwner().toString(), next.getChild(2)
				.getFirstChild().as(Label.class).getValue().trim());
		assertEquals("**Nested End**", next.getLastChild().as(Label.class)
				.getValue().trim());

		checkVerifier(next.getOwner(), HierarchyVerifier.class);
		next = next.getNextSibling();

		assertEquals("3. Last", next.as(Label.class).getValue().trim());

		next = next.getNextSibling();
		assertEquals("**Middle**", next.as(Label.class).getValue().trim());

		next = next.getNextSibling();
		assertEquals("1. First", next.as(Label.class).getValue().trim());

		next = next.getNextSibling();

		assertEquals(2, getShadowSize(next));

		assertEquals("**Nested Start**", next.getFirstChild().as(Label.class)
				.getValue().trim());
		assertEquals("background:yellow", next.getChild(1).as(Div.class)
				.getStyle());

		assertEquals(next.getChild(1).getOwner().toString(), next.getChild(1)
				.getFirstChild().as(Label.class).getValue().trim());

		assertEquals("background:green", next.getChild(2).as(Div.class)
				.getStyle());

		assertEquals(next.getChild(2).getOwner().toString(), next.getChild(2)
				.getFirstChild().as(Label.class).getValue().trim());
		assertEquals("**Nested End**", next.getLastChild().as(Label.class)
				.getValue().trim());

		checkVerifier(next.getOwner(), HierarchyVerifier.class);
		next = next.getNextSibling();

		assertEquals("3. Last", next.as(Label.class).getValue().trim());

		checkVerifier(hostAgent.getOwner(), HierarchyVerifier.class);
	}

	@Test
	public void testBasicMerge() {
		DesktopAgent desktop = connect(getTestURL("basicMerge.zul"));

		ComponentAgent hostAgent = desktop.query("#host");

		assertEquals(1, getShadowSize(hostAgent));
		assertEquals(2, hostAgent.getChildren().size());

		ComponentAgent next = hostAgent.getFirstChild();

		assertEquals("background:yellow", next.as(Div.class).getStyle());

		assertEquals(next.getOwner().toString(),
				next.getFirstChild().as(Label.class).getValue().trim());

		next = next.getNextSibling();

		assertEquals("background:green", next.as(Div.class).getStyle());

		assertEquals(next.getOwner().toString(),
				next.getFirstChild().as(Label.class).getValue().trim());

		checkVerifier(hostAgent.getOwner(), HierarchyVerifier.class);
	}

	@Test
	public void testBasicMerge2() {
		DesktopAgent desktop = connect(getTestURL("basicMerge2.zul"));

		ComponentAgent hostAgent = desktop.query("#host");

		assertEquals(1, getShadowSize(hostAgent));
		assertEquals(2, hostAgent.getChildren().size());

		ComponentAgent next = hostAgent.getFirstChild();

		assertEquals("background:yellow", next.as(Div.class).getStyle());

		assertEquals(next.getOwner().toString(),
				next.getFirstChild().as(Label.class).getValue().trim());

		next = next.getNextSibling();

		assertEquals("background:green", next.as(Div.class).getStyle());

		assertEquals(next.getOwner().toString(),
				next.getFirstChild().as(Label.class).getValue().trim());

		checkVerifier(hostAgent.getOwner(), HierarchyVerifier.class);
	}

	@Test
	public void testBasicMerge3() {
		DesktopAgent desktop = connect(getTestURL("basicMerge3.zul"));

		ComponentAgent hostAgent = desktop.query("#host");

		assertEquals(1, getShadowSize(hostAgent));
		assertEquals(1, hostAgent.getChildren().size());

		ComponentAgent next = hostAgent.getFirstChild();

		assertEquals("background:green", next.as(Div.class).getStyle());

		assertEquals(next.getOwner().toString(),
				next.getFirstChild().as(Label.class).getValue().trim());

		checkVerifier(hostAgent.getOwner(), HierarchyVerifier.class);
	}

	@Test
	public void testNestedMerge() {
		DesktopAgent desktop = connect(getTestURL("nestedMerge.zul"));

		ComponentAgent hostAgent = desktop.query("#host");

		assertEquals(1, getShadowSize(hostAgent));
		assertEquals(2, hostAgent.getChildren().size());

		ComponentAgent next = hostAgent.getFirstChild();

		assertEquals("background:yellow", next.as(Div.class).getStyle());

		// nested shadow
		next = next.getFirstChild();

		assertEquals("background:yellow", next.as(Div.class).getStyle());
		assertEquals(next.getOwner().toString(),
				next.getFirstChild().as(Label.class).getValue().trim());

		next = next.getNextSibling();

		assertEquals("background:green", next.as(Div.class).getStyle());

		assertEquals(next.getOwner().toString(),
				next.getFirstChild().as(Label.class).getValue().trim());

		checkVerifier(next.getOwner(), HierarchyVerifier.class);

		next = next.getParent().getNextSibling();

		assertEquals("background:green", next.as(Div.class).getStyle());
		assertEquals(next.getOwner().toString(),
				next.getFirstChild().as(Label.class).getValue().trim());

		checkVerifier(next.getOwner(), HierarchyVerifier.class);

	}

	@Test
	public void testMultiNestedMerge() {
		DesktopAgent desktop = connect(getTestURL("multiNestedMerge.zul"));

		ComponentAgent hostAgent = desktop.query("#host");

		assertEquals(2, getShadowSize(hostAgent));
		assertEquals(6, hostAgent.getChildren().size());

		ComponentAgent next = hostAgent.getFirstChild();

		assertEquals("**Start**", next.as(Label.class).getValue().trim());

		next = next.getNextSibling();

		assertEquals("background:yellow", next.as(Div.class).getStyle());

		// nested shadow
		next = next.getFirstChild();

		assertEquals("background:yellow", next.as(Div.class).getStyle());
		assertEquals(next.getOwner().toString(),
				next.getFirstChild().as(Label.class).getValue().trim());

		next = next.getNextSibling();

		assertEquals("background:green", next.as(Div.class).getStyle());

		assertEquals(next.getOwner().toString(),
				next.getFirstChild().as(Label.class).getValue().trim());

		checkVerifier(next.getOwner(), HierarchyVerifier.class);

		next = next.getParent().getNextSibling();

		assertEquals("background:green", next.as(Div.class).getStyle());
		assertEquals(next.getOwner().toString(),
				next.getFirstChild().as(Label.class).getValue().trim());

		next = next.getNextSibling();

		assertEquals("**Middle**", next.as(Label.class).getValue().trim());

		// nested shadow
		next = next.getNextSibling();
		assertEquals("background:yellow", next.as(Div.class).getStyle());
		
		next = next.getFirstChild();

		assertEquals("background:yellow", next.as(Div.class).getStyle());
		assertEquals(next.getOwner().toString(),
				next.getFirstChild().as(Label.class).getValue().trim());

		next = next.getNextSibling();

		assertEquals("background:green", next.as(Div.class).getStyle());

		assertEquals(next.getOwner().toString(),
				next.getFirstChild().as(Label.class).getValue().trim());

		checkVerifier(next.getOwner(), HierarchyVerifier.class);

		next = next.getParent().getNextSibling();

		assertEquals("**End**", next.as(Label.class).getValue().trim());

		checkVerifier(next.getOwner(), HierarchyVerifier.class);

	}


	@Test
	public void testUpdateMultiNestedHost() {
		DesktopAgent desktop = connect(getTestURL("multiNestedHost.zul"));

		ComponentAgent hostAgent = desktop.query("#host");

		assertEquals(2, getShadowSize(hostAgent));
		assertEquals(8, hostAgent.getChildren().size());

		hostAgent.getNextSibling().click();

		assertEquals(2, getShadowSize(hostAgent));
		assertEquals(8, hostAgent.getChildren().size());
		
		ComponentAgent next = hostAgent.getFirstChild();

		assertEquals("**Start**", next.as(Label.class).getValue().trim());

		next = next.getNextSibling();

		assertEquals("1. First", next.as(Label.class).getValue().trim());

		next = next.getNextSibling();

		assertEquals(2, getShadowSize(next));
		
		//nested shadow
		next = next.getFirstChild();
		
		assertEquals("**Nested Start**", next.as(Label.class).getValue().trim());
		
		next = next.getNextSibling();
		assertEquals("background:green", next.as(Div.class)
				.getStyle());

		assertEquals(next.getOwner().toString(), next.getFirstChild().as(Label.class).getValue().trim());
		
		next = next.getNextSibling();
		assertEquals("**Nested End**", next.as(Label.class)
				.getValue().trim());

		next = next.getParent();
		checkVerifier(next.getOwner(), HierarchyVerifier.class);
		
		next = next.getNextSibling();

		assertEquals("3. Last", next.as(Label.class).getValue().trim());

		next = next.getNextSibling();
		
		assertEquals("**Middle**", next.as(Label.class).getValue().trim());

		next = next.getNextSibling();
		assertEquals("1. First", next.as(Label.class).getValue().trim());

		next = next.getNextSibling();

		assertEquals(2, getShadowSize(next));

		//nested shadow
		next = next.getFirstChild();
				
		assertEquals("**Nested Start**", next.as(Label.class)
				.getValue().trim());

		next = next.getNextSibling();
		
		assertEquals(next.getOwner().toString(), next.getFirstChild().as(Label.class).getValue().trim());

		assertEquals("background:green", next.as(Div.class)
				.getStyle());

		next = next.getNextSibling();
		
		assertEquals(next.getOwner().toString(), next.getFirstChild().as(Label.class).getValue().trim());

		assertEquals("background:green", next.as(Div.class)
				.getStyle());
		
		next = next.getNextSibling();
		
		assertEquals("**Nested End**", next.as(Label.class)
				.getValue().trim());

		next = next.getParent();
		
		checkVerifier(next.getOwner(), HierarchyVerifier.class);
		
		next = next.getNextSibling();

		assertEquals("3. Last", next.as(Label.class).getValue().trim());

		checkVerifier(hostAgent.getOwner(), HierarchyVerifier.class);
	}
	


	@Test
	public void testUpdateMultiNestedMerge() {
		DesktopAgent desktop = connect(getTestURL("multiNestedMerge.zul"));

		ComponentAgent hostAgent = desktop.query("#host");

		assertEquals(2, getShadowSize(hostAgent));
		assertEquals(6, hostAgent.getChildren().size());

		hostAgent.getNextSibling().click();
		
		ComponentAgent next = hostAgent.getFirstChild();

		assertEquals("**Start**", next.as(Label.class).getValue().trim());

		next = next.getNextSibling();

		assertEquals("background:green", next.as(Div.class).getStyle());

		assertEquals(next.getOwner().toString(),
				next.getFirstChild().as(Label.class).getValue().trim());

		next = next.getNextSibling();

		assertEquals("**Middle**", next.as(Label.class).getValue().trim());

		next = next.getNextSibling();

		assertEquals("**End**", next.as(Label.class).getValue().trim());

		checkVerifier(next.getOwner(), HierarchyVerifier.class);

	}
}
