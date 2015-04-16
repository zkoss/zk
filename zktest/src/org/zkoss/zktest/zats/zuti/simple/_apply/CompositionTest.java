/** CompositionTest.java.

	Purpose:
		
	Description:
		
	History:
		4:06:00 PM Nov 21, 2014, Created by jumperchen

Copyright (C) 2014 Potix Corporation. All Rights Reserved.
 */
package org.zkoss.zktest.zats.zuti.simple._apply;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.zuti.ZutiBasicTestCase;
import org.zkoss.zktest.zats.zuti.verifier.NullShadowRoot;
import org.zkoss.zul.Label;
/**
 * @author jumperchen
 */
public class CompositionTest extends ZutiBasicTestCase {

	@Test
	public void testBasic() {
		DesktopAgent desktopAgent = this.connect(getTestURL("basic.zul"));
		ComponentAgent query = desktopAgent.query("label");
		assertNotNull(query);
		assertEquals(query.as(Label.class).getValue(), "test");
		checkVerifier(desktopAgent.query("div").getOwner(),
				NullShadowRoot.class);
	}

	@Test
	public void testBasicVariable() {
		DesktopAgent desktopAgent = this.connect(getTestURL("basicVariable.zul"));
		ComponentAgent query = desktopAgent.query("label");
		assertNotNull(query);
		assertEquals(query.as(Label.class).getValue(), "abc - abc");
		query = query.getNextSibling();
		assertEquals(query.getFirstChild().as(Label.class).getValue(), "fooName");
		checkVerifier(desktopAgent.query("div").getOwner(),
				NullShadowRoot.class);
	}

	@Test
	public void testNestedBasic() {
		DesktopAgent desktopAgent = this.connect(getTestURL("nestedBasic.zul"));
		ComponentAgent hostAgent = desktopAgent.query("#host");
		assertNotNull(hostAgent);
		assertEquals(3, hostAgent.getChildren().size());
		assertEquals(hostAgent.getFirstChild().as(Label.class).getValue()
				.trim(), "1. First");
		assertEquals(hostAgent.getChild(1).getFirstChild().as(Label.class)
				.getValue().trim(), "2.");
		assertEquals(hostAgent.getChild(1).getLastChild().as(Label.class)
				.getValue().trim(), "Hello");
		assertEquals(
				hostAgent.getLastChild().as(Label.class).getValue().trim(),
				"3. Last");
		checkVerifier(desktopAgent.query("div").getOwner(),
				NullShadowRoot.class);
	}

	@Test
	public void testWithNestedTemplateURI() {
		DesktopAgent desktopAgent = this
				.connect(getTestURL("withNestedTemplateURI.zul"));
		ComponentAgent hostAgent = desktopAgent.query("#host");
		assertNotNull(hostAgent);
		assertEquals(3, hostAgent.getChildren().size());
		assertEquals(hostAgent.getFirstChild().as(Label.class).getValue()
				.trim(), "1. First");
		assertEquals(hostAgent.getChild(1).getFirstChild().as(Label.class)
				.getValue().trim(), "With Template URI");
		assertEquals(
				hostAgent.getLastChild().as(Label.class).getValue().trim(),
				"3. Last");
		checkVerifier(desktopAgent.query("div").getOwner(),
				NullShadowRoot.class);
	}

	@Test
	public void testNestedMacro() {
		DesktopAgent desktopAgent = this.connect(getTestURL("nestedMacro.zul"));
		ComponentAgent hostAgent = desktopAgent.query("#host");
		assertNotNull(hostAgent);
		assertEquals(3, hostAgent.getChildren().size());
		assertEquals(hostAgent.getFirstChild().as(Label.class).getValue()
				.trim(), "1. First");
		assertEquals(hostAgent.getChild(1).getFirstChild().as(Label.class)
				.getValue().trim(), "With Template URI");
		assertEquals(
				hostAgent.getLastChild().as(Label.class).getValue().trim(),
				"3. Last");
		checkVerifier(desktopAgent.query("div").getOwner(),
				NullShadowRoot.class);
	}

	@Test
	public void testMultiMacroNested() {
		DesktopAgent desktopAgent = this.connect(getTestURL("multiNestedMacro.zul"));
		ComponentAgent hostAgent = desktopAgent.query("#host");
		assertNotNull(hostAgent);
		assertEquals(9, hostAgent.getChildren().size());
		ComponentAgent next = hostAgent.getFirstChild();
		assertEquals(next.as(Label.class).getValue().trim(), "**Start**");
		next = next.getNextSibling();
		assertEquals(next.as(Label.class).getValue().trim(), "1. First");
		next = next.getNextSibling();
		assertEquals(next.getFirstChild().as(Label.class).getValue().trim(), "With Template URI");
		next = next.getNextSibling();
		assertEquals(next.as(Label.class).getValue().trim(), "3. Last");
		next = next.getNextSibling();
		assertEquals(next.as(Label.class).getValue().trim(), "**Middle**");
		next = next.getNextSibling();
		assertEquals(next.as(Label.class).getValue().trim(), "1. First");
		next = next.getNextSibling();
		assertEquals(next.getFirstChild().as(Label.class).getValue().trim(), "With Template URI");
		next = next.getNextSibling();
		assertEquals(next.as(Label.class).getValue().trim(), "3. Last");
		next = next.getNextSibling();
		assertEquals(next.as(Label.class).getValue().trim(), "**End**");
		
		checkVerifier(desktopAgent.query("div").getOwner(),
				NullShadowRoot.class);
	}

	@Test
	public void testAnonymousTemplate() {
		DesktopAgent desktopAgent = this
				.connect(getTestURL("anonymousTemplate.zul"));
		ComponentAgent query = desktopAgent.query("label");
		assertNotNull(query);
		assertEquals(query.as(Label.class).getValue(), "test");
		checkVerifier(desktopAgent.query("div").getOwner(),
				NullShadowRoot.class);
	}

	@Test
	public void testWithTemplate() {
		DesktopAgent desktopAgent = this
				.connect(getTestURL("withTemplate.zul"));
		ComponentAgent query = desktopAgent.query("label");
		assertNotNull(query);
		assertEquals(query.as(Label.class).getValue(), "test with a name");
		checkVerifier(desktopAgent.query("div").getOwner(),
				NullShadowRoot.class);
	}

	@Test
	public void testWithTemplateURI() {
		DesktopAgent desktopAgent = this
				.connect(getTestURL("withTemplateURI.zul"));
		ComponentAgent query = desktopAgent.query("label");
		assertNotNull(query);
		assertEquals(query.as(Label.class).getValue(), "With Template URI");
		checkVerifier(desktopAgent.query("div").getOwner(),
				NullShadowRoot.class);
	}

	@Test
	public void testWithEL() {
		DesktopAgent desktopAgent = this.connect(getTestURL("withEL.zul"));
		ComponentAgent query = desktopAgent.query("label");
		assertNotNull(query);
		assertEquals(query.as(Label.class).getValue(),
				"test with the default name");
		checkVerifier(desktopAgent.query("div").getOwner(),
				NullShadowRoot.class);
	}

	@Test
	public void testMacro() {
		DesktopAgent desktopAgent = this.connect(getTestURL("macro.zul"));
		ComponentAgent query = desktopAgent.query("label");
		assertNotNull(query);
		assertEquals(query.as(Label.class).getValue(), "With Template URI");
		checkVerifier(desktopAgent.query("div").getOwner(),
				NullShadowRoot.class);
	}

	@Test
	public void testMultiMacro() {
		DesktopAgent desktopAgent = connect(getTestURL("multiMacro.zul"));
		ComponentAgent hostAgent = desktopAgent.query("#host");
		assertEquals(5, hostAgent.getChildren().size());
		ComponentAgent next = hostAgent.getFirstChild();
		assertEquals(next.as(Label.class).getValue().trim(), "**Start**");
		next = next.getNextSibling();
		assertEquals(next.getFirstChild().as(Label.class).getValue().trim(),
				"With Template URI");
		next = next.getNextSibling();
		assertEquals(next.as(Label.class).getValue().trim(), "**Middle**");
		next = next.getNextSibling();
		assertEquals(next.getFirstChild().as(Label.class).getValue().trim(),
				"With Template URI");
		next = next.getNextSibling();
		assertEquals(next.as(Label.class).getValue().trim(), "**End**");
		checkVerifier(desktopAgent.query("div").getOwner(),
				NullShadowRoot.class);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testMultiNestedApply() {
		DesktopAgent desktop = connect(getTestURL("multiNestedApply.zul"));
		ComponentAgent hostAgent = desktop.query("#host");
		assertEquals(10, hostAgent.getChildren().size());
		ComponentAgent next = hostAgent.getFirstChild();
		assertEquals(next.as(Label.class).getValue().trim(),
				"Without Template Name:");
		next = next.getNextSibling();
		assertEquals(next.getFirstChild().as(Label.class).getValue().trim(),
				"**Nested Start**");
		assertEquals(next.getChild(1).getFirstChild().as(Label.class)
				.getValue().trim(), "Without Template Name");
		assertEquals(next.getLastChild().as(Label.class).getValue().trim(),
				"**Nested End**");
		next = next.getNextSibling();
		assertEquals(next.as(Label.class).getValue().trim(),
				"With Template Name:");
		next = next.getNextSibling();
		assertEquals(next.getFirstChild().as(Label.class).getValue().trim(),
				"With Template Name");
		next = next.getNextSibling();
		assertEquals(next.as(Label.class).getValue().trim(),
				"With Template URI:");
		next = next.getNextSibling();
		assertEquals(next.as(Label.class).getValue().trim(),
				"1. First");
		next = next.getNextSibling();
		assertEquals(next.getFirstChild().as(Label.class)
				.getValue().trim(), "With Template URI");
		next = next.getNextSibling();
		assertEquals(next.as(Label.class).getValue().trim(),
				"3. Last");
		next = next.getNextSibling();
		assertEquals(next.as(Label.class).getValue().trim(),
				"With Host's Template Name:");
		next = next.getNextSibling();
		assertEquals(next.getFirstChild().as(Label.class).getValue().trim(),
				"**Nested Host's template Start**");
		assertEquals(next.getChild(1).getFirstChild().as(Label.class)
				.getValue().trim(), "With Host's Template Name");
		assertEquals(next.getLastChild().as(Label.class).getValue().trim(),
				"**Nested Host's template End**");

		checkVerifier(hostAgent.getOwner(), NullShadowRoot.class);
	}
}
