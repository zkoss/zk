/** CompositionTest.java.

	Purpose:
		
	Description:
		
	History:
		2:26:10 PM Nov 27, 2014, Created by jumperchen

Copyright (C) 2014 Potix Corporation. All Rights Reserved.
 */
package org.zkoss.zktest.zats.zuti.simple._foreach;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

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
	public void testNestedBasic() {
		DesktopAgent desktop = connect(getTestURL("nestedBasic.zul"));

		ComponentAgent hostAgent = desktop.query("#host");
		checkChildren(hostAgent, new Integer[] { 0, 1, 2, 3 }, 1, new Integer[] { 0, 1}, 1);
		hostAgent = hostAgent.getNextSibling();
		checkChildren(hostAgent, new Integer[] { 0, 1, 2, 3 }, 2, new Integer[] { 0, 1}, 2);
		hostAgent = hostAgent.getNextSibling();
		checkChildren(hostAgent, new Object[] { "one", "two", "three" }, 1, new Object[] { "one", "two", "three" }, 1);
		hostAgent = hostAgent.getNextSibling();
		checkChildren(hostAgent, new Object[] { "two", "three" }, 1, new Object[] { "two", "three" }, 1);
	}

	private void checkChildren(ComponentAgent parent, Object[] data, int step, Object[] nestedData, int nestedStep) {
		List<ComponentAgent> children = parent.getChildren();
		assertEquals(data.length / step + 1, children.size());
		
		int index = 0;
		for (ListIterator<ComponentAgent> it = parent.getChildren()
				.listIterator(1); it.hasNext(); index += step) {
			ComponentAgent cmp = it.next();
			assertEquals("color:blue", cmp.as(Div.class).getStyle());
			assertEquals("**Start**", cmp.getFirstChild().as(Label.class).getValue().trim());
			assertEquals("**End**", cmp.getLastChild().as(Label.class).getValue().trim());
			for (int i = 0, childIndex = 1, j = nestedData.length; i < j; i += nestedStep, childIndex++) {
				assertEquals(data[index] + " - " + nestedData[i] + " Template",
						cmp.getChild(childIndex).getFirstChild().as(Label.class).getValue().trim());
			}
		}

		checkVerifier(parent.getOwner(), NullShadowRoot.class);
	}
	
	@Test
	public void testMultiRoot() {
		DesktopAgent desktop = connect(getTestURL("multiRoot.zul"));

		ComponentAgent hostAgent = desktop.query("#host");
		
		assertEquals(13, hostAgent.getChildren().size());
		
		ComponentAgent next = hostAgent.getFirstChild();
		assertEquals("**Start**", next.as(Label.class).getValue().trim());
		next = next.getNextSibling();
		for (int i = 0; i <= 3; i++) {
			for (int j = 0; j <= 1; j++) {
				assertEquals("color:blue", next.as(Div.class).getStyle());
				assertEquals(i + " - " + j + " Template", next.getFirstChild().as(Label.class).getValue().trim());
				next = next.getNextSibling();
			}
		}
		
		assertEquals("**Middle**", next.as(Label.class).getValue().trim());
		next = next.getNextSibling();
		for (int i = 0; i <= 3; i+=2) {
			assertEquals("color:blue", next.as(Div.class).getStyle());
			assertEquals(i + " Template", next.getFirstChild().as(Label.class).getValue().trim());
			next = next.getNextSibling();
		}
		assertEquals("**End**", next.as(Label.class).getValue().trim());

		checkVerifier(hostAgent.getOwner(), NullShadowRoot.class);
	}

	
	@Test
	public void testMultiNestedRoot() {
		DesktopAgent desktop = connect(getTestURL("multiNestedRoot.zul"));

		ComponentAgent hostAgent = desktop.query("#host");
		
		assertEquals(8, hostAgent.getChildren().size());
		
		ComponentAgent next = hostAgent.getFirstChild();
		assertEquals("**Start**", next.as(Label.class).getValue().trim());
		next = next.getNextSibling();
		List<String> items = Arrays.asList(new String[] { "one", "two", "three" });
		for (int i = 0, e = items.size(); i < e; i++) {
			assertEquals("color:blue", next.as(Div.class).getStyle());
			next = next.getFirstChild();
			assertEquals("**Nested Start**", next.as(Label.class).getValue().trim());
			next = next.getNextSibling();
			for (int j = 0; j <= 1; j++) {
				assertEquals("color:blue", next.as(Div.class).getStyle());
				assertEquals(items.get(i) + " - " + j + " Template", next.getFirstChild().as(Label.class).getValue().trim());
				next = next.getNextSibling();
			}

			assertEquals("**Nested Middle**", next.as(Label.class).getValue().trim());
			next = next.getNextSibling();
			for (int j = 2; j <= 3; j++) {
				assertEquals("color:blue", next.as(Div.class).getStyle());
				assertEquals(items.get(i) + " - " + j + " Template", next.getFirstChild().as(Label.class).getValue().trim());
				next = next.getNextSibling();
			}
			assertEquals("**Nested End**", next.as(Label.class).getValue().trim());
			
			next = next.getParent();
			checkVerifier(next.getOwner(), NullShadowRoot.class);
			
			next = next.getNextSibling();
		}

		
		assertEquals("**Middle**", next.as(Label.class).getValue().trim());
		next = next.getNextSibling();
		for (int i = 0; i <= 3; i+=2) {
			assertEquals("color:blue", next.as(Div.class).getStyle());
			assertEquals(i + " Template", next.getFirstChild().as(Label.class).getValue().trim());
			next = next.getNextSibling();
		}
		assertEquals("**End**", next.as(Label.class).getValue().trim());

		checkVerifier(hostAgent.getOwner(), NullShadowRoot.class);
	}
}
