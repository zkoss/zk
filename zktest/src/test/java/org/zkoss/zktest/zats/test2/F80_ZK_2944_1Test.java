/* F80_ZK_2944_1Test.java

	Purpose:
		
	Description:
		
	History:
		Wed, Jan  6, 2016  6:02:52 PM, Created by Christopher

Copyright (C) 2016 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zktest.zats.test2;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.Vlayout;
import org.zkoss.zuti.zul.If;

/**
 * 
 * @author Christopher
 */
public class F80_ZK_2944_1Test extends ZATSTestCase {
	
	/*
	 *  final page structure, templates will be discarded
	 *  div
	 *  	vlayout
	 *  		label
	 *  		vlayout (shadow host)
	 *  			if (shadow root)
	 *  				label
	 *  			if (shadow root)
	 *  				label
	 */
	
	@Test
	public void testHostNoParam() {
		DesktopAgent desktop = connect();
		Component parent = desktop.query("#parent").as(Div.class);
		List<Component> comps = Selectors.find(parent, ":host");
		Assertions.assertTrue(comps.size() == 1,
				"expecting 1, got: " + comps.size());
		Assertions.assertTrue(comps.get(0).getClass().equals(Vlayout.class));
	}
	
	@Test
	public void testVlayout() {
		DesktopAgent desktop = connect();
		Component parent = desktop.query("#parent").as(Div.class);
		List<Component> comps = Selectors.find(parent, "vlayout");
		Assertions.assertTrue(comps.size() == 2,
				"expecting 2, got: " + comps.size());
		Assertions.assertTrue(comps.get(0).getClass().equals(Vlayout.class));
		Assertions.assertTrue(comps.get(1).getClass().equals(Vlayout.class));
	}
	
	@Test
	public void testShadowRoots() {
		DesktopAgent desktop = connect();
		Component parent = desktop.query("#parent").as(Div.class);
		List<Component> comps = Selectors.find(parent, "::shadow");
		Assertions.assertTrue(comps.size() == 2,
				"expecting 2, got: " + comps.size());
		Assertions.assertTrue(comps.get(0).getClass().equals(If.class));
		Assertions.assertTrue(comps.get(1).getClass().equals(If.class));
	}
	
	@Test
	public void testNonShadowChild() {
		DesktopAgent desktop = connect();
		Component parent = desktop.query("#parent").as(Div.class);
		List<Component> comps = Selectors.find(parent, "* > label");
		Assertions.assertTrue(comps.size() == 3,
				"expecting 3, got: " + comps.size());
		Assertions.assertTrue(comps.get(0).getClass().equals(Label.class));
		Assertions.assertTrue(comps.get(1).getClass().equals(Label.class));
		Assertions.assertTrue(comps.get(2).getClass().equals(Label.class));
	}
	
	@Test
	public void testShadowGeneralSibling() {
		DesktopAgent desktop = connect();
		Component parent = desktop.query("#parent").as(Div.class);
		List<Component> comps = Selectors.find(parent, ":host::shadow ~ if");
		Assertions.assertTrue(comps.size() == 1,
				"expecting 1, got: " + comps.size());
		Assertions.assertTrue(comps.get(0).getClass().equals(If.class));
	}
	
	@Test
	public void testShadowAdjacentSibling() {
		DesktopAgent desktop = connect();
		Component parent = desktop.query("#parent").as(Div.class);
		List<Component> comps = Selectors.find(parent, ":host::shadow + if");
		Assertions.assertTrue(comps.size() == 1,
				"expecting 1, got: " + comps.size());
		Assertions.assertTrue(comps.get(0).getClass().equals(If.class));
	}
	
	@Test
	public void testVlayoutFirstChild() {
		DesktopAgent desktop = connect();
		Component parent = desktop.query("#parent").as(Div.class);
		List<Component> comps = Selectors.find(parent, "vlayout > *:first-child");
		Assertions.assertTrue(comps.size() == 2,
				"expecting 2, got: " + comps.size());
		Assertions.assertTrue(comps.get(0).getClass().equals(Label.class));
		Assertions.assertTrue(comps.get(1).getClass().equals(Label.class));
	}
	
	@Test
	public void testLabel() {
		DesktopAgent desktop = connect();
		Component parent = desktop.query("#parent").as(Div.class);
		List<Component> comps = Selectors.find(parent, "label");
		Assertions.assertTrue(comps.size() == 3,
				"expecting 3, got: " + comps.size());
		Assertions.assertTrue(comps.get(0).getClass().equals(Label.class));
		Assertions.assertTrue(comps.get(1).getClass().equals(Label.class));
		Assertions.assertTrue(comps.get(2).getClass().equals(Label.class));
	}
}
