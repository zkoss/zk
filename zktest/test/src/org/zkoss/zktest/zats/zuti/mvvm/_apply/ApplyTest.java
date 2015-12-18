/** ApplyTest.java.

	Purpose:
		
	Description:
		
	History:
		9:50:26 AM Nov 25, 2014, Created by jumperchen

Copyright (C) 2014 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.zuti.mvvm._apply;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.zuti.ZutiBasicTestCase;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;


/**
 * @author jumperchen
 *
 */
public class ApplyTest extends ZutiBasicTestCase {
	@Test
	public void testResult() {
		DesktopAgent desktop = connect();
		ComponentAgent hostAgent = desktop.query("#host");
		assertTrue(hostAgent.getChildren().size() == 1);
		assertTrue(getShadowSize(hostAgent) == 1);
		List<ComponentAgent> queryAll = desktop.queryAll("row");
		assertEquals(queryAll.size(), 4);

		assertEquals("Foo", queryAll.get(0).getChild(1).getFirstChild().as(Label.class).getValue());
		assertEquals("foo.zkoss.com", queryAll.get(1).getChild(1).getFirstChild().as(Label.class).getValue());
		assertEquals("[Foo][foo.zkoss.com]", queryAll.get(2).getChild(1).getFirstChild().as(Label.class).getValue());
	}

	@Test
	public void testResultWithInjection() {
		DesktopAgent desktop = connect(getTestURL("applyInjection.zul"));
		ComponentAgent hostAgent = desktop.query("#host");
		assertTrue(hostAgent.getChildren().size() == 1);
		assertTrue(getShadowSize(hostAgent) == 1);
		List<ComponentAgent> queryAll = desktop.queryAll("row");
		assertEquals(queryAll.size(), 4);
		List<ComponentAgent> columnQueryAll = desktop.queryAll("column");
		assertEquals(columnQueryAll.size(), 2);

		assertEquals("Foo", queryAll.get(0).getChild(1).getFirstChild().as(Label.class).getValue());
		assertEquals("foo.zkoss.com", queryAll.get(1).getChild(1).getFirstChild().as(Label.class).getValue());
		assertEquals("[Foo][foo.zkoss.com]", queryAll.get(2).getChild(1).getFirstChild().as(Label.class).getValue());
	}
	@Test
	public void testEditResult() {
		DesktopAgent desktop = connect();
		ComponentAgent hostAgent = desktop.query("#host");
		hostAgent.query("grid button").click();
		
		assertTrue(hostAgent.getChildren().size() == 1);
		assertTrue(getShadowSize(hostAgent) == 1);
		List<ComponentAgent> queryAll = desktop.queryAll("row");
		assertEquals(queryAll.size(), 4);

		assertEquals("Foo", queryAll.get(0).getChild(1).getFirstChild().as(Textbox.class).getValue());
		assertEquals("foo.zkoss.com", queryAll.get(1).getChild(1).getFirstChild().as(Textbox.class).getValue());
		assertEquals("[Foo][foo.zkoss.com]", queryAll.get(2).getChild(1).getFirstChild().as(Label.class).getValue());
		
		queryAll.get(0).getChild(1).getFirstChild().type("Test");
		hostAgent.query("grid button").click();
		
		queryAll = desktop.queryAll("row");
		assertEquals("Test", queryAll.get(0).getChild(1).getFirstChild().as(Label.class).getValue());
		assertEquals("[Test][foo.zkoss.com]", queryAll.get(2).getChild(1).getFirstChild().as(Label.class).getValue());
	}
}
