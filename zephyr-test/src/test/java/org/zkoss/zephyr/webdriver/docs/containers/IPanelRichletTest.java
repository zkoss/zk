/* IPanelRichletTest.java

	Purpose:

	Description:

	History:
		Mon Apr 18 17:16:44 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.docs.containers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.stateless.sul.IPanel;
import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * A set of unit test for {@link IPanel} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Containers/Panel">Panel</a>,
 * if any.
 *
 * @author katherine
 * @see IPanel
 */
public class IPanelRichletTest extends WebDriverTestCase {
	@Test
	public void border() {
		connect("/containers/ipanel/border");
		assertFalse(jq(".z-panel-noborder").exists());
		click(jq("@button"));
		waitResponse();
		assertTrue(jq(".z-panel-noborder").exists());
	}

	@Test
	public void bottomToolbar() {
		connect("/containers/ipanel/bottomToolbar");
		assertTrue(jq(".z-panel-bottom:eq(0)").children(".z-toolbar-panel").exists());
		assertFalse(jq(".z-panel-bottom:eq(1)").children(".z-toolbar-panel").exists());
	}

	@Test
	public void caption() {
		connect("/containers/ipanel/caption");
		assertEquals("panel caption", jq(".z-panel").find(".z-caption").text());
	}

	@Test
	public void closable() {
		connect("/containers/ipanel/closable");
		assertTrue(jq(".z-panel-close").exists());
		click(jq("@button"));
		waitResponse();
		assertFalse(jq(".z-panel-close").exists());
	}

	@Test
	public void collapsible() {
		connect("/containers/ipanel/collapsible");
		assertTrue(jq(".z-panel-expand").exists());
		click(jq("@button"));
		waitResponse();
		assertFalse(jq(".z-panel-expand").exists());
	}

	@Test
	public void floatable() {
		connect("/containers/ipanel/floatable");
		int offsetLeft = jq(".z-button").offsetLeft();
		clickAt(jq("@button"), 55, 0);
		waitResponse();
		assertTrue(offsetLeft < jq(".z-button").offsetLeft());
	}

	@Test
	public void footToolbar() {
		connect("/containers/ipanel/footToolbar");
		assertTrue(jq(".z-panel-bottom").children(".z-toolbar-panel").exists());
	}

	@Test
	public void maximizable() {
		connect("/containers/ipanel/maximizable");
		assertTrue(jq(".z-panel-maximize").exists());
		click(jq("@button"));
		waitResponse();
		assertFalse(jq(".z-panel-maximize").exists());
	}

	@Test
	public void maximized() {
		connect("/containers/ipanel/maximized");
		assertTrue(jq(".z-panel-maximized").exists());
		click(jq("@button"));
		waitResponse();
		assertFalse(jq(".z-panel-maximized").exists());
	}

	@Test
	public void minheight() {
		connect("/containers/ipanel/minheight");
		String script = "zk.$('$panel')._minheight";
		assertEquals("200", WebDriverTestCase.getEval(script));
		click(jq("@button"));
		waitResponse();
		assertEquals("300", WebDriverTestCase.getEval(script));
	}

	@Test
	public void minimizable() {
		connect("/containers/ipanel/minimizable");
		assertTrue(jq(".z-panel-minimize").exists());
		click(jq("@button"));
		waitResponse();
		assertFalse(jq(".z-panel-minimize").exists());
	}

	@Test
	public void minimized() {
		connect("/containers/ipanel/minimized");
		assertTrue(jq(".z-panel").isVisible());
		click(jq("@button"));
		waitResponse();
		assertFalse(jq(".z-panel").isVisible());
	}

	@Test
	public void minwidth() {
		connect("/containers/ipanel/minwidth");
		String script = "zk.$('$panel')._minwidth";
		assertEquals("200", WebDriverTestCase.getEval(script));
		click(jq("@button"));
		waitResponse();
		assertEquals("100", WebDriverTestCase.getEval(script));
	}

	@Test
	public void movable() {
		connect("/containers/ipanel/movable");
		String script = "zk.$('$panel')._movable";
		assertEquals("true", WebDriverTestCase.getEval(script));
		clickAt(jq("@button"), 45, 0);
		waitResponse();
		assertEquals("false", WebDriverTestCase.getEval(script));
	}

	@Test
	public void nativeScrollbar() {
		connect("/containers/ipanel/nativeScrollbar");
		jq(".z-panelchildren").scrollTop(10);
		waitResponse();
		assertEquals(10, jq(".z-panelchildren").scrollTop());
	}

	@Test
	public void open() {
		connect("/containers/ipanel/open");
		assertFalse(jq(".z-panel-body").isVisible());
		click(jq("@button"));
		waitResponse();
		assertTrue(jq(".z-panel-body").isVisible());
	}

	@Test
	public void panelchildren() {
		connect("/containers/ipanel/panelchildren");
		assertEquals("Content1Content2", jq(".z-panel").find(".z-panelchildren").text());
	}

	@Test
	public void sizable() {
		connect("/containers/ipanel/sizable");
		String script = "zk.$('$panel')._sizable";
		assertEquals("true", WebDriverTestCase.getEval(script));
		click(jq("@button"));
		waitResponse();
		assertEquals("false", WebDriverTestCase.getEval(script));
	}

	@Test
	public void title() {
		connect("/containers/ipanel/title");
		assertEquals("panel", jq(".z-panel-header").text());
		click(jq("@button"));
		waitResponse();
		assertEquals("new panel", jq(".z-panel-header").text());
	}

	@Test
	public void topToolbar() {
		connect("/containers/ipanel/topToolbar");
		assertTrue(jq(".z-panel-top").children(".z-toolbar-panel").exists());
	}
}