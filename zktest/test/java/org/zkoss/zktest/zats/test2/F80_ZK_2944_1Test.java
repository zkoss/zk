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

import org.junit.Assert;
import org.junit.Test;
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
		Assert.assertTrue("expecting 1, got: " + comps.size(), comps.size() == 1);
		Assert.assertTrue(comps.get(0).getClass().equals(Vlayout.class));
	}
	
	@Test
	public void testVlayout() {
		DesktopAgent desktop = connect();
		Component parent = desktop.query("#parent").as(Div.class);
		List<Component> comps = Selectors.find(parent, "vlayout");
		Assert.assertTrue("expecting 2, got: " + comps.size(), comps.size() == 2);
		Assert.assertTrue(comps.get(0).getClass().equals(Vlayout.class));
		Assert.assertTrue(comps.get(1).getClass().equals(Vlayout.class));
	}
	
	@Test
	public void testShadowRoots() {
		DesktopAgent desktop = connect();
		Component parent = desktop.query("#parent").as(Div.class);
		List<Component> comps = Selectors.find(parent, "::shadow");
		Assert.assertTrue("expecting 2, got: " + comps.size(), comps.size() == 2);
		Assert.assertTrue(comps.get(0).getClass().equals(If.class));
		Assert.assertTrue(comps.get(1).getClass().equals(If.class));
	}
	
	@Test
	public void testNonShadowChild() {
		DesktopAgent desktop = connect();
		Component parent = desktop.query("#parent").as(Div.class);
		List<Component> comps = Selectors.find(parent, "* > label");
		Assert.assertTrue("expecting 3, got: " + comps.size(), comps.size() == 3);
		Assert.assertTrue(comps.get(0).getClass().equals(Label.class));
		Assert.assertTrue(comps.get(1).getClass().equals(Label.class));
		Assert.assertTrue(comps.get(2).getClass().equals(Label.class));
	}
	
	@Test
	public void testShadowGeneralSibling() {
		DesktopAgent desktop = connect();
		Component parent = desktop.query("#parent").as(Div.class);
		List<Component> comps = Selectors.find(parent, ":host::shadow ~ if");
		Assert.assertTrue("expecting 1, got: " + comps.size(), comps.size() == 1);
		Assert.assertTrue(comps.get(0).getClass().equals(If.class));
	}
	
	@Test
	public void testShadowAdjacentSibling() {
		DesktopAgent desktop = connect();
		Component parent = desktop.query("#parent").as(Div.class);
		List<Component> comps = Selectors.find(parent, ":host::shadow + if");
		Assert.assertTrue("expecting 1, got: " + comps.size(), comps.size() == 1);
		Assert.assertTrue(comps.get(0).getClass().equals(If.class));
	}
	
	@Test
	public void testVlayoutFirstChild() {
		DesktopAgent desktop = connect();
		Component parent = desktop.query("#parent").as(Div.class);
		List<Component> comps = Selectors.find(parent, "vlayout > *:first-child");
		Assert.assertTrue("expecting 2, got: " + comps.size(), comps.size() == 2);
		Assert.assertTrue(comps.get(0).getClass().equals(Label.class));
		Assert.assertTrue(comps.get(1).getClass().equals(Label.class));
	}
	
	@Test
	public void testLabel() {
		DesktopAgent desktop = connect();
		Component parent = desktop.query("#parent").as(Div.class);
		List<Component> comps = Selectors.find(parent, "label");
		Assert.assertTrue("expecting 3, got: " + comps.size(), comps.size() == 3);
		Assert.assertTrue(comps.get(0).getClass().equals(Label.class));
		Assert.assertTrue(comps.get(1).getClass().equals(Label.class));
		Assert.assertTrue(comps.get(2).getClass().equals(Label.class));
	}
}
