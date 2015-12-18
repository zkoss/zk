/** CompositionTest.java.

	Purpose:
		
	Description:
		
	History:
		6:10:00 PM Dec 1, 2014, Created by jumperchen

Copyright (C) 2014 Potix Corporation. All Rights Reserved.
 */
package org.zkoss.zktest.zats.zuti.mvvm._choose;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.zuti.ZutiBasicTestCase;
import org.zkoss.zktest.zats.zuti.verifier.HierarchyVerifier;
import org.zkoss.zul.Button;
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
		List<ComponentAgent> xChildren = hostAgent.getChildren();
		assertEquals(9, xChildren.size());
		
		int x = 1;
		for (Iterator<ComponentAgent> xit = xChildren.iterator(); xit.hasNext(); x++) {
			ComponentAgent xChild = xit.next();
			if (x < 3) {
				assertEquals("color:blue", xChild.as(Div.class).getStyle());
			} else if (x < 6) {
				assertEquals("color:yellow", xChild.as(Div.class).getStyle());
			} else {
				assertEquals("color:red", xChild.as(Div.class).getStyle());
			}
			assertEquals(x + " Template", xChild.getFirstChild().as(Label.class).getValue().trim());
		}
		checkVerifier(hostAgent.getOwner(), HierarchyVerifier.class);
	}
	@Test
	public void testBasicMerge() {

		DesktopAgent desktop = connect(getTestURL("basicMerge.zul"));

		ComponentAgent hostAgent = desktop.query("#host");
		List<ComponentAgent> xChildren = hostAgent.getChildren();
		assertEquals(9, xChildren.size());
		assertEquals(1, getAllShadowSize(hostAgent));
		
		int x = 1;
		for (Iterator<ComponentAgent> xit = xChildren.iterator(); xit.hasNext(); x++) {
			ComponentAgent xChild = xit.next();
			if (x < 3) {
				assertEquals("color:blue", xChild.as(Div.class).getStyle());
			} else if (x < 6) {
				assertEquals("color:yellow", xChild.as(Div.class).getStyle());
			} else {
				assertEquals("color:red", xChild.as(Div.class).getStyle());
			}
			assertEquals(x + " Template", xChild.getFirstChild().as(Label.class).getValue().trim());
		}
		checkVerifier(hostAgent.getOwner(), HierarchyVerifier.class);
	}
	@Test
	public void testMultiRoot() {

		DesktopAgent desktop = connect(getTestURL("multiRoot.zul"));

		ComponentAgent hostAgent = desktop.query("#host");
		List<ComponentAgent> xChildren = hostAgent.getChildren();
		assertEquals(29, xChildren.size());
		
		Iterator<ComponentAgent> xit = xChildren.iterator();
		assertEquals("**Start**", xit.next().as(Label.class).getValue().trim());
		for (int x = 1; x < 10; x++) {
			ComponentAgent xChild = xit.next();
			if (x < 3) {
				assertEquals("color:blue", xChild.as(Div.class).getStyle());
			} else if (x < 6) {
				assertEquals("color:yellow", xChild.as(Div.class).getStyle());
			} else {
				assertEquals("color:red", xChild.as(Div.class).getStyle());
			}
			assertEquals(x + " Template", xChild.getFirstChild().as(Label.class).getValue().trim());

			assertEquals("**Middle**", xit.next().as(Label.class).getValue().trim());

			xChild = xit.next();
			if (x < 3) {
				assertEquals("color:blue", xChild.as(Div.class).getStyle());
			} else if (x < 6) {
				assertEquals("color:yellow", xChild.as(Div.class).getStyle());
			} else {
				assertEquals("color:red", xChild.as(Div.class).getStyle());
			}
			assertEquals(x + " Template", xChild.getFirstChild().as(Label.class).getValue().trim());
		}
		assertEquals("**End**", xit.next().as(Label.class).getValue().trim());
		checkVerifier(hostAgent.getOwner(), HierarchyVerifier.class);
	}
	
	@Test
	public void testMultiRootMerge() {

		DesktopAgent desktop = connect(getTestURL("multiRootMerge.zul"));

		ComponentAgent hostAgent = desktop.query("#host");
		List<ComponentAgent> xChildren = hostAgent.getChildren();
		assertEquals(33, xChildren.size());
		
		Iterator<ComponentAgent> xit = xChildren.iterator();
		assertEquals("**Start**", xit.next().as(Label.class).getValue().trim());
		for (int x = 1; x < 10; x++) {
			ComponentAgent xChild = xit.next();
			if (x < 3) {
				assertEquals("color:blue", xChild.as(Div.class).getStyle());
			} else if (x < 6) {
				assertEquals("color:yellow", xChild.as(Div.class).getStyle());
			} else {
				assertEquals("color:red", xChild.as(Div.class).getStyle());
			}
			assertEquals(x + " Template", xChild.getFirstChild().as(Label.class).getValue().trim());

			assertEquals("**Middle**", xit.next().as(Label.class).getValue().trim());

			xChild = xit.next();
			if (x < 3) {
				assertEquals("color:blue", xChild.as(Div.class).getStyle());
			} else if (x < 6) {
				assertEquals("color:yellow", xChild.as(Div.class).getStyle());
			} else {
				assertEquals("color:red", xChild.as(Div.class).getStyle());
			}
			assertEquals(x + " Template", xChild.getFirstChild().as(Label.class).getValue().trim());
			
			if (x >= 6) {
				// button
				xChild = xit.next();
				assertTrue(xChild.getOwner() instanceof Button);
				try {
					xChild.click();
				} catch (Exception ex) {
					fail("No exception here!");
				}
			}
		}
		assertEquals("**End**", xit.next().as(Label.class).getValue().trim());
		assertEquals(41, getAllShadowSize(hostAgent));
		checkVerifier(hostAgent.getOwner(), HierarchyVerifier.class);
	}
	@Test
	public void testBasicNested() {

		DesktopAgent desktop = connect(getTestURL("basicNested.zul"));

		ComponentAgent hostAgent = desktop.query("#host");
		List<ComponentAgent> xChildren = hostAgent.getChildren();
		assertEquals(9, xChildren.size());
		
		int x = 1;
		for (Iterator<ComponentAgent> xit = xChildren.iterator(); xit.hasNext(); x++) {
			ComponentAgent xChild = xit.next().getFirstChild();
			switch (x % 3) {
			case 1:
				assertEquals("color:blue", xChild.as(Div.class).getStyle());
				break;
			case 2:
				assertEquals("color:yellow", xChild.as(Div.class).getStyle());
				break;
			default:
				assertEquals("color:red", xChild.as(Div.class).getStyle());
			}
			assertEquals(x + " Template", xChild.getFirstChild().as(Label.class).getValue().trim());
		}
		checkVerifier(hostAgent.getOwner(), HierarchyVerifier.class);
	}
	@Test
	public void testBasicNestedMerge() {

		DesktopAgent desktop = connect(getTestURL("basicNestedMerge.zul"));

		ComponentAgent hostAgent = desktop.query("#host");
		List<ComponentAgent> xChildren = hostAgent.getChildren();
		assertEquals(9, xChildren.size());
		assertEquals(37, getAllShadowSize(hostAgent));
		
		int x = 1;
		for (Iterator<ComponentAgent> xit = xChildren.iterator(); xit.hasNext(); x++) {
			ComponentAgent xChild = xit.next().getFirstChild();
			switch (x % 3) {
			case 1:
				assertEquals("color:blue", xChild.as(Div.class).getStyle());
				break;
			case 2:
				assertEquals("color:yellow", xChild.as(Div.class).getStyle());
				break;
			default:
				assertEquals("color:red", xChild.as(Div.class).getStyle());
			}
			assertEquals(x + " Template", xChild.getFirstChild().as(Label.class).getValue().trim());
		}
		checkVerifier(hostAgent.getOwner(), HierarchyVerifier.class);
	}
	@Test
	public void testUpdateBasicNested() {

		DesktopAgent desktop = connect(getTestURL("updateBasicNested.zul"));

		ComponentAgent hostAgent = desktop.query("#host");
		List<ComponentAgent> xChildren = hostAgent.getChildren();
		assertEquals(2, xChildren.size());
		assertEquals(4, getShadowSize(hostAgent));
		ComponentAgent next = hostAgent.getFirstChild().getFirstChild();
		assertEquals("background:gray", next.as(Div.class).getStyle());
		assertEquals("Template", next.getFirstChild().as(Label.class).getValue().trim());
		
		hostAgent.getLastChild().click();

		next = hostAgent.getFirstChild().getFirstChild();
		assertEquals("background:green", next.as(Div.class).getStyle());
		assertEquals("Template", next.getFirstChild().as(Label.class).getValue().trim());
		checkVerifier(hostAgent.getOwner(), HierarchyVerifier.class);
	}
	@Test
	public void testUpdateMultiRoot() {

		DesktopAgent desktop = connect(getTestURL("updateMultiRoot.zul"));

		ComponentAgent hostAgent = desktop.query("#host");
		List<ComponentAgent> xChildren = hostAgent.getChildren();
		assertEquals(6, xChildren.size());
		assertEquals(16, getShadowSize(hostAgent));
		ComponentAgent next = hostAgent.getFirstChild();

		assertEquals("**Start**", next.as(Label.class).getValue().trim());
		next = next.getNextSibling();
		
		assertEquals("background:blue", next.as(Div.class).getStyle());
		assertEquals("Template", next.getFirstChild().as(Label.class).getValue().trim());
		
		next = next.getNextSibling();
		assertEquals("**Middle**", next.as(Label.class).getValue().trim());

		next = next.getNextSibling();
		
		assertEquals("background:blue", next.as(Div.class).getStyle());
		assertEquals("Template", next.getFirstChild().as(Label.class).getValue().trim());

		next = next.getNextSibling();
		assertEquals("**End**", next.as(Label.class).getValue().trim());

		checkVerifier(hostAgent.getOwner(), HierarchyVerifier.class);
		
		hostAgent.getLastChild().click();
		
		next = hostAgent.getFirstChild();

		assertEquals("**Start**", next.as(Label.class).getValue().trim());
		next = next.getNextSibling();
		
		assertEquals("background:brown", next.as(Div.class).getStyle());
		assertEquals("Template", next.getFirstChild().as(Label.class).getValue().trim());
		
		next = next.getNextSibling();
		assertEquals("**Middle**", next.as(Label.class).getValue().trim());

		next = next.getNextSibling();
		
		assertEquals("background:brown", next.as(Div.class).getStyle());
		assertEquals("Template", next.getFirstChild().as(Label.class).getValue().trim());

		next = next.getNextSibling();
		assertEquals("**End**", next.as(Label.class).getValue().trim());

		checkVerifier(hostAgent.getOwner(), HierarchyVerifier.class);
	}
	@Test
	public void testUpdateMultiNestedRoot() {

		DesktopAgent desktop = connect(getTestURL("updateMultiNestedRoot.zul"));

		ComponentAgent hostAgent = desktop.query("#host");
		List<ComponentAgent> xChildren = hostAgent.getChildren();
		assertEquals(6, xChildren.size());
		assertEquals(8, getShadowSize(hostAgent));
		ComponentAgent next = hostAgent.getFirstChild();

		assertEquals("**Start**", next.as(Label.class).getValue().trim());
		next = next.getNextSibling().getFirstChild();
		
		assertEquals("background:blue", next.as(Div.class).getStyle());
		assertEquals("Template", next.getFirstChild().as(Label.class).getValue().trim());
		
		checkVerifier(next.getParent().getOwner(), HierarchyVerifier.class);
		next = next.getParent().getNextSibling();
		assertEquals("**Middle**", next.as(Label.class).getValue().trim());

		next = next.getNextSibling().getFirstChild();
		
		assertEquals("background:blue", next.as(Div.class).getStyle());
		assertEquals("Template", next.getFirstChild().as(Label.class).getValue().trim());

		checkVerifier(next.getParent().getOwner(), HierarchyVerifier.class);
		next = next.getParent().getNextSibling();
		assertEquals("**End**", next.as(Label.class).getValue().trim());

		checkVerifier(hostAgent.getOwner(), HierarchyVerifier.class);
		
		hostAgent.getLastChild().click();
		
		next = hostAgent.getFirstChild();

		assertEquals("**Start**", next.as(Label.class).getValue().trim());
		next = next.getNextSibling().getFirstChild();
		
		assertEquals("background:green", next.as(Div.class).getStyle());
		assertEquals("Template", next.getFirstChild().as(Label.class).getValue().trim());
		
		checkVerifier(next.getParent().getOwner(), HierarchyVerifier.class);
		next = next.getParent().getNextSibling();
		assertEquals("**Middle**", next.as(Label.class).getValue().trim());

		next = next.getNextSibling().getFirstChild();
		
		assertEquals("background:green", next.as(Div.class).getStyle());
		assertEquals("Template", next.getFirstChild().as(Label.class).getValue().trim());

		checkVerifier(next.getParent().getOwner(), HierarchyVerifier.class);
		next = next.getParent().getNextSibling();
		assertEquals("**End**", next.as(Label.class).getValue().trim());
		
		checkVerifier(hostAgent.getOwner(), HierarchyVerifier.class);
	}
	@Test
	public void testMultiNestedRoot() {

		DesktopAgent desktop = connect(getTestURL("multiNestedRoot.zul"));

		ComponentAgent hostAgent = desktop.query("#host");
		List<ComponentAgent> xChildren = hostAgent.getChildren();
		assertEquals(29, xChildren.size());
		Iterator<ComponentAgent> xit = xChildren.iterator();
		assertEquals("**Start**", xit.next().as(Label.class).getValue().trim());
		
		for (int x = 1; x <= 9; x++) {
			ComponentAgent xChild = xit.next();
			xChild = xChild.getFirstChild();
			assertEquals("**Nested Start**", xChild.as(Label.class).getValue().trim());
			xChild = xChild.getNextSibling();
			switch (x % 3) {
			case 1:
				assertEquals("color:blue", xChild.as(Div.class).getStyle());
				break;
			case 2:
				assertEquals("color:yellow", xChild.as(Div.class).getStyle());
				break;
			default:
				assertEquals("color:red", xChild.as(Div.class).getStyle());
			}
			assertEquals(x + " Template", xChild.getFirstChild().as(Label.class).getValue().trim());
			xChild = xChild.getNextSibling();
			assertEquals("**Nested Middle**", xChild.as(Label.class).getValue().trim());
			xChild = xChild.getNextSibling();
			switch (x % 3) {
			case 1:
				assertEquals("color:blue", xChild.as(Div.class).getStyle());
				break;
			case 2:
				assertEquals("color:yellow", xChild.as(Div.class).getStyle());
				break;
			default:
				assertEquals("color:red", xChild.as(Div.class).getStyle());
			}
			assertEquals(x + " Template", xChild.getFirstChild().as(Label.class).getValue().trim());
			xChild = xChild.getNextSibling();
			assertEquals("**Nested End**", xChild.as(Label.class).getValue().trim());
			
			assertEquals("**Middle**", xit.next().as(Label.class).getValue().trim());
			
			xChild = xit.next();
			xChild = xChild.getFirstChild();
			assertEquals("**Nested Start**", xChild.as(Label.class).getValue().trim());
			xChild = xChild.getNextSibling();
			switch (x % 3) {
			case 1:
				assertEquals("color:blue", xChild.as(Div.class).getStyle());
				break;
			case 2:
				assertEquals("color:yellow", xChild.as(Div.class).getStyle());
				break;
			default:
				assertEquals("color:red", xChild.as(Div.class).getStyle());
			}
			assertEquals(x + " Template", xChild.getFirstChild().as(Label.class).getValue().trim());
			xChild = xChild.getNextSibling();
			assertEquals("**Nested Middle**", xChild.as(Label.class).getValue().trim());
			xChild = xChild.getNextSibling();
			switch (x % 3) {
			case 1:
				assertEquals("color:blue", xChild.as(Div.class).getStyle());
				break;
			case 2:
				assertEquals("color:yellow", xChild.as(Div.class).getStyle());
				break;
			default:
				assertEquals("color:red", xChild.as(Div.class).getStyle());
			}
			assertEquals(x + " Template", xChild.getFirstChild().as(Label.class).getValue().trim());
			xChild = xChild.getNextSibling();
			assertEquals("**Nested End**", xChild.as(Label.class).getValue().trim());
		}
		checkVerifier(hostAgent.getOwner(), HierarchyVerifier.class);
	}
	@Test
	public void testMultiNestedMerge() {

		DesktopAgent desktop = connect(getTestURL("multiNestedMerge.zul"));

		ComponentAgent hostAgent = desktop.query("#host");
		List<ComponentAgent> xChildren = hostAgent.getChildren();
		assertEquals(29, xChildren.size());
		Iterator<ComponentAgent> xit = xChildren.iterator();
		assertEquals("**Start**", xit.next().as(Label.class).getValue().trim());
		
		for (int x = 1; x <= 9; x++) {
			ComponentAgent xChild = xit.next();
			xChild = xChild.getFirstChild();
			assertEquals("**Nested Start**", xChild.as(Label.class).getValue().trim());
			xChild = xChild.getNextSibling();
			switch (x % 3) {
			case 1:
				assertEquals("color:blue", xChild.as(Div.class).getStyle());
				break;
			case 2:
				assertEquals("color:yellow", xChild.as(Div.class).getStyle());
				break;
			default:
				assertEquals("color:red", xChild.as(Div.class).getStyle());
			}
			assertEquals(x + " Template", xChild.getFirstChild().as(Label.class).getValue().trim());
			xChild = xChild.getNextSibling();
			if (x == 6) {
				assertTrue(xChild.getOwner() instanceof Button);
				try {
					xChild.click();
				} catch (Exception ex) {
					fail("No exception here!");
				}
				xChild = xChild.getNextSibling();
			}
			assertEquals("**Nested Middle**", xChild.as(Label.class).getValue().trim());
			xChild = xChild.getNextSibling();
			switch (x % 3) {
			case 1:
				assertEquals("color:blue", xChild.as(Div.class).getStyle());
				break;
			case 2:
				assertEquals("color:yellow", xChild.as(Div.class).getStyle());
				break;
			default:
				assertEquals("color:red", xChild.as(Div.class).getStyle());
			}
			assertEquals(x + " Template", xChild.getFirstChild().as(Label.class).getValue().trim());
			xChild = xChild.getNextSibling();
			assertEquals("**Nested End**", xChild.as(Label.class).getValue().trim());
			
			assertEquals("**Middle**", xit.next().as(Label.class).getValue().trim());
			
			xChild = xit.next();
			xChild = xChild.getFirstChild();
			assertEquals("**Nested Start**", xChild.as(Label.class).getValue().trim());
			xChild = xChild.getNextSibling();
			switch (x % 3) {
			case 1:
				assertEquals("color:blue", xChild.as(Div.class).getStyle());
				break;
			case 2:
				assertEquals("color:yellow", xChild.as(Div.class).getStyle());
				break;
			default:
				assertEquals("color:red", xChild.as(Div.class).getStyle());
			}
			assertEquals(x + " Template", xChild.getFirstChild().as(Label.class).getValue().trim());
			xChild = xChild.getNextSibling();
			assertEquals("**Nested Middle**", xChild.as(Label.class).getValue().trim());
			xChild = xChild.getNextSibling();
			switch (x % 3) {
			case 1:
				assertEquals("color:blue", xChild.as(Div.class).getStyle());
				break;
			case 2:
				assertEquals("color:yellow", xChild.as(Div.class).getStyle());
				break;
			default:
				assertEquals("color:red", xChild.as(Div.class).getStyle());
			}
			assertEquals(x + " Template", xChild.getFirstChild().as(Label.class).getValue().trim());
			xChild = xChild.getNextSibling();
			assertEquals("**Nested End**", xChild.as(Label.class).getValue().trim());
		}
		assertEquals(2, getAllShadowSize(hostAgent));
		checkVerifier(hostAgent.getOwner(), HierarchyVerifier.class);
	}
	@Test
	public void testUpdate() {

		DesktopAgent desktop = connect(getTestURL("update.zul"));

		ComponentAgent hostAgent = desktop.query("#host");
		List<ComponentAgent> xChildren = hostAgent.getChildren();
		assertEquals(10, xChildren.size());
		
		hostAgent.getLastChild().click();
		xChildren = hostAgent.getChildren();
		assertEquals(6, xChildren.size());
		int x = 5;
		for (Iterator<ComponentAgent> xit = xChildren.iterator(); xit.hasNext(); x++) {
			ComponentAgent xChild = xit.next();
			if (x < 3) {
				assertEquals("color:blue", xChild.as(Div.class).getStyle());
			} else if (x < 6) {
				assertEquals("color:yellow", xChild.as(Div.class).getStyle());
			} else {
				assertEquals("color:red", xChild.as(Div.class).getStyle());
			}
			assertEquals(x + " Template", xChild.getFirstChild().as(Label.class).getValue().trim());
			if (x == 9)
				break;
		}
		checkVerifier(hostAgent.getOwner(), HierarchyVerifier.class);
		
		
	}
}
