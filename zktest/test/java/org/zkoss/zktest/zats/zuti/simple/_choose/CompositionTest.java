/** CompositionTest.java.

	Purpose:
		
	Description:
		
	History:
		10:12:19 AM Nov 27, 2014, Created by jumperchen

Copyright (C) 2014 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.zuti.simple._choose;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Iterator;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.zuti.ZutiBasicTestCase;
import org.zkoss.zktest.zats.zuti.verifier.NullShadowRoot;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;

/**
 * @author jumperchen
 *
 */
public class CompositionTest extends ZutiBasicTestCase {

	@Test
	public void testBasic() {
		DesktopAgent desktopAgent = this.connect(getTestURL("basic.zul"));
		ComponentAgent query = desktopAgent.query("label");
		assertNotNull(query);
		assertEquals(query.as(Label.class).getValue(), "1 Template");
		assertEquals(query.getParent().as(Div.class).getStyle(), "color:green");
		checkVerifier(desktopAgent.query("div").getOwner(), NullShadowRoot.class);
	}

	@Test
	public void testMultiRoot() {
		DesktopAgent desktopAgent = this.connect(getTestURL("multiRoot.zul"));
		ComponentAgent hostAgent = desktopAgent.query("#host");
		assertNotNull(hostAgent);
		assertEquals(5, hostAgent.getChildren().size());

		ComponentAgent next = hostAgent.getFirstChild();
		
		assertEquals("**Start**", next.as(Label.class).getValue().trim());
		
		next = next.getNextSibling();
		assertEquals(next.as(Div.class).getStyle(), "color:green");
		assertEquals(next.getFirstChild().as(Label.class).getValue().trim(), "1 Template");
		
		next = next.getNextSibling();
		assertEquals("**Middle**", next.as(Label.class).getValue().trim());

		next = next.getNextSibling();
		assertEquals(next.as(Div.class).getStyle(), "color:red");
		assertEquals(next.getFirstChild().as(Label.class).getValue().trim(), "1 Template");
		
		next = next.getNextSibling();
		assertEquals("**End**", next.as(Label.class).getValue().trim());
		checkVerifier(desktopAgent.query("div").getOwner(), NullShadowRoot.class);
	}

	@Test
	public void testMultiNestedRoot() {
		DesktopAgent desktopAgent = this
				.connect(getTestURL("multiNestedRoot.zul"));
		ComponentAgent host = desktopAgent.query("#host");
		assertEquals(5, host.getChildren().size());

		ComponentAgent next = host.getFirstChild();
		assertEquals("**Start**", next.as(Label.class).getValue().trim());
		
		next = next.getNextSibling();
		assertEquals("color:green", next.as(Div.class).getStyle());
		assertEquals("1. First", next.getFirstChild().as(Label.class).getValue().trim());
		assertEquals("color:blue", next.getChild(1).as(Div.class).getStyle());
		assertEquals("1 Template", next.getChild(1).getFirstChild().as(Label.class).getValue().trim());
		
		assertEquals("2. Center", next.getChild(2).as(Label.class).getValue().trim());
		
		assertEquals("color:blue", next.getChild(3).as(Div.class).getStyle());
		assertEquals("1 Template", next.getChild(3).getFirstChild().as(Label.class).getValue().trim());
		assertEquals("3. Last", next.getChild(4).as(Label.class).getValue().trim());

		next = next.getNextSibling();
		assertEquals("**Middle**", next.as(Label.class).getValue().trim());
		
		next = next.getNextSibling();
		assertEquals("color:green", next.as(Div.class).getStyle());
		assertEquals("1. First", next.getFirstChild().as(Label.class).getValue().trim());
		assertEquals("color:blue", next.getChild(1).as(Div.class).getStyle());
		assertEquals("1 Template", next.getChild(1).getFirstChild().as(Label.class).getValue().trim());
		
		assertEquals("2. Center", next.getChild(2).as(Label.class).getValue().trim());
		
		assertEquals("color:blue", next.getChild(3).as(Div.class).getStyle());
		assertEquals("1 Template", next.getChild(3).getFirstChild().as(Label.class).getValue().trim());
		assertEquals("3. Last", next.getChild(4).as(Label.class).getValue().trim());
		
		next = next.getNextSibling();
		assertEquals("**End**", next.as(Label.class).getValue().trim());
		
		checkVerifier(host.getOwner(), NullShadowRoot.class);
	}

	@Test
	public void testWithEL() {
		DesktopAgent desktopAgent = this
				.connect(getTestURL("withEL.zul"));
		ComponentAgent query = desktopAgent.query("label");
		assertNotNull(query);
		assertEquals(query.as(Label.class).getValue(), "1 Template");
		assertEquals(query.getParent().as(Div.class).getStyle(), "color:green");
		checkVerifier(desktopAgent.query("div").getOwner(), NullShadowRoot.class);
	}

	@Test
	public void testWithSameValue() {
		DesktopAgent desktopAgent = this
				.connect(getTestURL("withSameValue.zul"));
		ComponentAgent query = desktopAgent.query("label");
		assertNotNull(query);
		assertEquals(query.as(Label.class).getValue(), "1 Template");
		assertEquals(query.getParent().as(Div.class).getStyle(), "color:blue");
		checkVerifier(desktopAgent.query("div").getOwner(), NullShadowRoot.class);
	}

	@Test
	public void testNested() {
		DesktopAgent desktopAgent = this
				.connect(getTestURL("nested.zul"));
		ComponentAgent host = desktopAgent.query("#host");
		assertEquals(1, host.getChildren().size());

		assertEquals(host.getFirstChild().getFirstChild().as(Label.class).getValue().trim(), "1. First");
		assertEquals(host.getFirstChild().getLastChild().as(Label.class).getValue().trim(), "3. Last");
		assertEquals(host.getFirstChild().getChild(1).as(Div.class).getStyle(), "color:green");
		assertEquals(host.getFirstChild().getChild(1).getFirstChild().as(Label.class).getValue().trim(), "1 Template");
		checkVerifier(host.getOwner(), NullShadowRoot.class);
	}

	@Test
	public void testNestedWithForEach() {
		DesktopAgent desktopAgent = this
				.connect(getTestURL("nestedWithForEach.zul"));
		ComponentAgent host = desktopAgent.query("#host");
		assertEquals(12, host.getChildren().size());
		Iterator<ComponentAgent> it = host.getChildren().iterator();
		String[] colors = {"color:blue", "color:green", "color:red", "color:red"};
		for (int i = 0; i <= 3; i++) {
			assertEquals("**Start**", it.next().as(Label.class).getValue().trim());
			checkNested(it.next(), i, colors[i]);
			assertEquals("**End**", it.next().as(Label.class).getValue().trim());
		}
		checkVerifier(host.getOwner(), NullShadowRoot.class);
	}
	
	private void checkNested(ComponentAgent next, int each, String color) {
		assertEquals(next.getFirstChild().as(Label.class).getValue().trim(), "1. First");
		assertEquals(next.getLastChild().as(Label.class).getValue().trim(), "3. Last");
		assertEquals(next.getChild(1).as(Div.class).getStyle(), color);
		assertEquals(next.getChild(1).getFirstChild().as(Label.class).getValue().trim(), each + " Template");
	}
}
