/** CompositionTest.java.

	Purpose:
		
	Description:
		
	History:
		5:13:01 PM Nov 27, 2014, Created by jumperchen

Copyright (C) 2014 Potix Corporation. All Rights Reserved.
 */
package org.zkoss.zktest.zats.zuti.mvvm._foreach;

import static org.junit.Assert.assertEquals;

import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zhtml.Text;
import org.zkoss.zktest.zats.zuti.ZutiBasicTestCase;
import org.zkoss.zktest.zats.zuti.verifier.HierarchyVerifier;
import org.zkoss.zktest.zats.zuti.verifier.NullShadowRoot;
import org.zkoss.zul.Label;

/**
 * @author jumperchen
 */
public class CompositionTest extends ZutiBasicTestCase {
	@Test
	public void testNestedForEach() {

		DesktopAgent desktop = connect(getTestURL("nestedForEach.zul"));

		ComponentAgent hostAgent = desktop.query("#host");
		List<ComponentAgent> xChildren = hostAgent.getChildren();
		int x = 1;
		for (Iterator<ComponentAgent> xit = xChildren.iterator(); xit.hasNext();) {
			ComponentAgent xChild = xit.next();
			if (xChild.getOwner() instanceof Text)
				continue;
			List<ComponentAgent> yChildren = xChild.getChildren();
			int y = 1;
			for (Iterator<ComponentAgent> yit = yChildren.iterator(); yit
					.hasNext();) {
				ComponentAgent yChild = yit.next();
				if (yChild.getOwner() instanceof Text)
					continue;
				assertEquals(x + " X " + y + " = " + (x * y), yChild
						.getFirstChild().as(Label.class).getValue());
				y++;
			}
			checkVerifier(xChild.getOwner(), HierarchyVerifier.class);
			x++;
		}
		checkVerifier(hostAgent.getOwner(), HierarchyVerifier.class);
	}
	@Test
	public void testBasicWithApply() {

		DesktopAgent desktop = connect(getTestURL("basicWithApply.zul"));

		ComponentAgent hostAgent = desktop.query("#host");
		List<ComponentAgent> xChildren = hostAgent.getChildren();
		int x = 1;
		
		assertEquals(6, xChildren.size());
		for (Iterator<ComponentAgent> xit = xChildren.iterator(); xit.hasNext();) {
			ComponentAgent xChild = xit.next();
			String prefix = xChild.as(Label.class).getValue();
			xChild = xit.next();
			prefix += xChild.as(Label.class).getValue();
			assertEquals("Test " + x, prefix.trim());
			x++;
		}
		checkVerifier(hostAgent.getOwner(), HierarchyVerifier.class);
	}

	@Test
	public void testUpdateBasicWithApply() {

		DesktopAgent desktop = connect(getTestURL("basicWithApply.zul"));

		ComponentAgent hostAgent = desktop.query("#host");
		ComponentAgent small = hostAgent.getNextSibling();
		ComponentAgent large = small.getNextSibling();
		large.click();
		
		List<ComponentAgent> xChildren = hostAgent.getChildren();
		int x = 0;
		
		assertEquals(30, xChildren.size());
		for (Iterator<ComponentAgent> xit = xChildren.iterator(); xit.hasNext();) {
			ComponentAgent xChild = xit.next();
			String prefix = xChild.as(Label.class).getValue();
			xChild = xit.next();
			prefix += xChild.as(Label.class).getValue();
			assertEquals("Test "+ x +"@"+ x + "@zkoss.org", prefix.trim());
			x++;
		}
		checkVerifier(hostAgent.getOwner(), HierarchyVerifier.class);
		
		small.click();

		x = 0;
		xChildren = hostAgent.getChildren();
		assertEquals(6, xChildren.size());
		for (Iterator<ComponentAgent> xit = xChildren.iterator(); xit.hasNext();) {
			ComponentAgent xChild = xit.next();
			String prefix = xChild.as(Label.class).getValue();
			xChild = xit.next();
			prefix += xChild.as(Label.class).getValue();
			assertEquals("Test "+ x +"@"+ x + "@zkoss.org", prefix.trim());
			x++;
		}
		checkVerifier(hostAgent.getOwner(), HierarchyVerifier.class);
	}
	
	@Test
	public void testNestedMerge() {

		DesktopAgent desktop = connect(getTestURL("nestedMerge.zul"));

		ComponentAgent hostAgent = desktop.query("#host");
		List<ComponentAgent> xChildren = hostAgent.getChildren();
		int x = 1;
		for (Iterator<ComponentAgent> xit = xChildren.iterator(); xit.hasNext();) {
			ComponentAgent xChild = xit.next();
			if (xChild.getOwner() instanceof Text)
				continue;
			List<ComponentAgent> yChildren = xChild.getChildren();
			int y = 1;
			for (Iterator<ComponentAgent> yit = yChildren.iterator(); yit
					.hasNext();) {
				ComponentAgent yChild = yit.next();
				if (yChild.getOwner() instanceof Text)
					continue;
				assertEquals(x + " X " + y + " = " + (x * y), yChild
						.getFirstChild().as(Label.class).getValue());
				y++;
			}
			checkVerifier(xChild.getOwner(), NullShadowRoot.class);
			x++;
		}
		checkVerifier(hostAgent.getOwner(), HierarchyVerifier.class);
	}
	
	@Test
	public void testUpdateNestedForEach() {

		DesktopAgent desktop = connect(getTestURL("nestedForEach.zul"));

		ComponentAgent hostAgent = desktop.query("#host");

		ComponentAgent buttonAgent = hostAgent.getNextSibling();
		
		for (int i = 0, begin = 1; i < 2; i++, begin += 4) {
			buttonAgent.click();
	
			List<ComponentAgent> xChildren = hostAgent.getChildren();
			int x = begin;
			for (Iterator<ComponentAgent> xit = xChildren.iterator(); xit.hasNext();) {
				ComponentAgent xChild = xit.next();
				if (xChild.getOwner() instanceof Text)
					continue;
				List<ComponentAgent> yChildren = xChild.getChildren();
				int y = begin;
				for (Iterator<ComponentAgent> yit = yChildren.iterator(); yit
						.hasNext();) {
					ComponentAgent yChild = yit.next();
					if (yChild.getOwner() instanceof Text)
						continue;
					assertEquals(x + " X " + y + " = " + (x * y), yChild
							.getFirstChild().as(Label.class).getValue());
					y++;
				}
				x++;
			}
			checkVerifier(hostAgent.getOwner(), HierarchyVerifier.class);
			buttonAgent = buttonAgent.getNextSibling();
		}
	}
	
	@Test
	public void testBasic() {
		DesktopAgent desktop = connect(getTestURL("basic.zul"));

		ComponentAgent hostAgent = desktop.query("#host");
		
		assertEquals(5, hostAgent.getChildren().size());
		
		ComponentAgent next = hostAgent.getFirstChild();
		for (int i = 1; i <= 9; i+=2) {
			assertEquals(String.valueOf(i), next.as(Label.class).getValue());
			next = next.getNextSibling();
		}

		checkVerifier(hostAgent.getOwner(), HierarchyVerifier.class);
	}
	
	@Test
	public void testBasicMerge() {
		DesktopAgent desktop = connect(getTestURL("basicMerge.zul"));

		ComponentAgent hostAgent = desktop.query("#host");
		
		assertEquals(10, hostAgent.getChildren().size());
		assertEquals(2, getShadowSize(hostAgent));
		
		ComponentAgent next = hostAgent.getFirstChild();
		for (int z = 1; z <= 3; z+=2) {
			for (int i = 1; i <= 9; i+=2) {
				assertEquals(String.valueOf(i), next.as(Label.class).getValue());
				next = next.getNextSibling();
			}
		}

		checkVerifier(hostAgent.getOwner(), HierarchyVerifier.class);
	}
	
	@Test
	public void testMultiRoot() {
		DesktopAgent desktop = connect(getTestURL("multiRoot.zul"));

		ComponentAgent hostAgent = desktop.query("#host");
		
		assertEquals(10, hostAgent.getChildren().size());
		assertEquals(2, getShadowSize(hostAgent));
		
		updateMultiRoot(hostAgent.getFirstChild(), 1, 9, 2, false);
		checkVerifier(hostAgent.getOwner(), HierarchyVerifier.class);
	}
	
	@Test
	public void testMultiRootMerge() {
		DesktopAgent desktop = connect(getTestURL("multiRootMerge.zul"));

		ComponentAgent hostAgent = desktop.query("#host");
		
		assertEquals(15, hostAgent.getChildren().size());
		assertEquals(1, getShadowSize(hostAgent));
		
		updateMultiRoot(hostAgent.getFirstChild(), 1, 9, 1, false);
		checkVerifier(hostAgent.getOwner(), HierarchyVerifier.class);
	}
	
	@Test
	public void testUpdateMultiRoot() {
		DesktopAgent desktop = connect(getTestURL("multiRoot.zul"));

		ComponentAgent hostAgent = desktop.query("#host");
		
		assertEquals(10, hostAgent.getChildren().size());
		assertEquals(2, getShadowSize(hostAgent));
		
		ComponentAgent button = hostAgent.getParent().getLastChild();
		
		button.click();

		updateMultiRoot(hostAgent.getFirstChild(), 1, 9, 2);
		checkVerifier(hostAgent.getOwner(), HierarchyVerifier.class);

		button = button.getPreviousSibling();
		button.click();
		
		updateMultiRoot(hostAgent.getFirstChild(), 1, 9, 3);
		checkVerifier(hostAgent.getOwner(), HierarchyVerifier.class);
		
		button = button.getPreviousSibling();
		button.click();
		
		updateMultiRoot(hostAgent.getFirstChild(), 5, 9, 3);
		checkVerifier(hostAgent.getOwner(), HierarchyVerifier.class);
		
		button = button.getPreviousSibling();
		button.click();
		updateMultiRoot(hostAgent.getFirstChild(), 1, 5, 3);
		checkVerifier(hostAgent.getOwner(), HierarchyVerifier.class);
	}
	private void updateMultiRoot(ComponentAgent next, int begin, int end, int step) {
		updateMultiRoot(next, begin, end, step, true);
	}
	private void updateMultiRoot(ComponentAgent next, int begin, int end, int step, boolean before) {
		assertEquals("**Start**", next.as(Label.class).getValue().trim());
		next = next.getNextSibling();
		
		for (int i = begin; i <= end; i+=step) {
			assertEquals(String.valueOf(i), next.as(Label.class).getValue());
			next = next.getNextSibling();
		}
		
		assertEquals("**Middle**", next.as(Label.class).getValue().trim());
		next = next.getNextSibling();

		for (int i = begin; i <= end; i += step + 3) {
			if (before) {
				assertEquals(" + " + String.valueOf(i), next.as(Label.class).getValue());
			} else {
				assertEquals(String.valueOf(i) + " + ", next.as(Label.class).getValue());
			}
			next = next.getNextSibling();
		}
		
		assertEquals("**End**", next.as(Label.class).getValue().trim());
	}
	private void updateMultiNestedRoot(ComponentAgent next, int begin, int end, int step) {
		updateMultiNestedRoot(next, begin, end, step, true);
	}
	private void updateMultiNestedRoot(ComponentAgent next, int begin, int end, int step, boolean before) {
		assertEquals("**Start**", next.as(Label.class).getValue().trim());
		next = next.getNextSibling();
		
		for (int s = begin; s <= end; s+=step) {
			next = next.getFirstChild();
			assertEquals("**Nested Start**", next.as(Label.class).getValue().trim());
			next = next.getNextSibling();
			for (int i = begin; i <= end; i+=step + 3) {
				if (!before) {
					assertEquals(String.valueOf(i), next.as(Label.class).getValue());
				} else {
					assertEquals("", next.as(Label.class).getValue());
				}
				next = next.getNextSibling();
			}
			assertEquals("**Nested Middle**", next.as(Label.class).getValue().trim());
			next = next.getNextSibling();
	
			if (before) {
				for (int i = begin; i <= end; i += step + 3) {
					assertEquals(String.valueOf(i), next.as(Label.class).getValue());
					next = next.getNextSibling();
				}
			}
			assertEquals("**Nested End**", next.as(Label.class).getValue().trim());
			
			next = next.getParent().getNextSibling();
		}
		
		assertEquals("**Middle**", next.as(Label.class).getValue().trim());
		
		next = next.getNextSibling();
		for (int i = begin; i <= end; i += step + 3) {
			if (before) {
				assertEquals(" + " + String.valueOf(i), next.as(Label.class).getValue());
			} else {
				assertEquals(String.valueOf(i) + " + ", next.as(Label.class).getValue());
			}
			next = next.getNextSibling();
		}
		assertEquals("**End**", next.as(Label.class).getValue().trim());
	}
	
	@Test
	public void testMultiNestedRoot() {
		DesktopAgent desktop = connect(getTestURL("multiNestedRoot.zul"));

		ComponentAgent hostAgent = desktop.query("#host");
		
		assertEquals(10, hostAgent.getChildren().size());
		
		updateMultiNestedRoot(hostAgent.getFirstChild(), 1, 9, 2, false);
		checkVerifier(hostAgent.getOwner(), HierarchyVerifier.class);
	}
	
	@Test
	public void testMultiNestedRootMerge() {
		DesktopAgent desktop = connect(getTestURL("multiNestedRootMerge.zul"));

		ComponentAgent hostAgent = desktop.query("#host");
		
		assertEquals(10, hostAgent.getChildren().size());
		assertEquals(6, getAllShadowSize(hostAgent));
		
		updateMultiNestedRoot(hostAgent.getFirstChild(), 1, 9, 2, false);
		checkVerifier(hostAgent.getOwner(), HierarchyVerifier.class);
	}
	@Test
	public void testUpdateMultiNestedRoot() {
		DesktopAgent desktop = connect(getTestURL("multiNestedRoot.zul"));

		ComponentAgent hostAgent = desktop.query("#host");
		
		assertEquals(10, hostAgent.getChildren().size());
		
		ComponentAgent button = hostAgent.getParent().getLastChild();
		
		button.click();

		updateMultiNestedRoot(hostAgent.getFirstChild(), 1, 9, 2);
		checkVerifier(hostAgent.getOwner(), HierarchyVerifier.class);

		button = button.getPreviousSibling();
		button.click();
		
		updateMultiNestedRoot(hostAgent.getFirstChild(), 1, 9, 3);
		checkVerifier(hostAgent.getOwner(), HierarchyVerifier.class);
		
		button = button.getPreviousSibling();
		button.click();
		
		updateMultiNestedRoot(hostAgent.getFirstChild(), 5, 9, 3);
		checkVerifier(hostAgent.getOwner(), HierarchyVerifier.class);
		
		button = button.getPreviousSibling();
		button.click();
		updateMultiNestedRoot(hostAgent.getFirstChild(), 1, 5, 3);
		checkVerifier(hostAgent.getOwner(), HierarchyVerifier.class);
	}
}
