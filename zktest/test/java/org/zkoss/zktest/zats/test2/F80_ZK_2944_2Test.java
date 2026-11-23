/* F80_ZK_2944_2Test.java

	Purpose:
		
	Description:
		
	History:
		Wed, Jan  6, 2016  6:03:04 PM, Created by Christopher

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
import org.zkoss.zuti.zul.If;

/**
 * 
 * @author Christopher
 */
public class F80_ZK_2944_2Test extends ZATSTestCase {
	/*
	 *  init page structure
	 *  div
	 *  	div (shadow host)
	 *  		if (shadow root) (detach)
	 *  			if
	 *  				label
	 *  				if
	 *  					label
	 *  					label
	 *  			if (detach)
	 *  				label
	 *  			if
	 *  				label
	 *  		if (shadow root)
	 *  			if (detach)
	 *  				if
	 *  					if (detach)
	 *  						label
	 *  
	 *  final page structure, some ifs are not preserved
	 *  div
	 *  	div (shadow host)
	 *  		if (shadow root)
	 *  			label
	 *  			if
	 *  				label
	 *  				label
	 *  		label
	 *  		if (shadow root)
	 *  			label
	 *  		if (shadow root)
	 *  			if
	 *  				label
	 */
	
	@Test
	public void testHostNoParam() {
		DesktopAgent desktop = connect();
		Component parent = desktop.query("#parent").as(Div.class);
		List<Component> comps = Selectors.find(parent, ":host");
		Assert.assertTrue("expecting 1, got: " + comps.size(), comps.size() == 1);
		Assert.assertTrue(comps.get(0).getClass().equals(Div.class));
		Assert.assertTrue(comps.get(0).getId().equals("host"));
	}
	
	@Test
	public void testRootNoParam() {
		DesktopAgent desktop = connect();
		Component parent = desktop.query("#parent").as(Div.class);
		List<Component> comps = Selectors.find(parent, "::shadow");
		Assert.assertTrue("expecting 3, got: " + comps.size(), comps.size() == 3);
		Assert.assertTrue(comps.get(0).getClass().equals(If.class));
		Assert.assertTrue(comps.get(0).getId().equals("if2"));
		Assert.assertTrue(comps.get(1).getClass().equals(If.class));
		Assert.assertTrue(comps.get(1).getId().equals("if5"));
		Assert.assertTrue(comps.get(2).getClass().equals(If.class));
		Assert.assertTrue(comps.get(2).getId().equals("if6"));
	}
	
	@Test
	public void testNonShadowChild() {
		DesktopAgent desktop = connect();
		Component parent = desktop.query("#parent").as(Div.class);
		List<Component> comps = Selectors.find(parent, ":host > label");
		Assert.assertTrue("expecting 6, got: " + comps.size(), comps.size() == 6);
		Assert.assertTrue(comps.get(0).getClass().equals(Label.class));
		Assert.assertTrue(comps.get(0).getId().equals("lb1"));
		Assert.assertTrue(comps.get(1).getClass().equals(Label.class));
		Assert.assertTrue(comps.get(1).getId().equals("lb2"));
		Assert.assertTrue(comps.get(2).getClass().equals(Label.class));
		Assert.assertTrue(comps.get(2).getId().equals("lb3"));
		Assert.assertTrue(comps.get(3).getClass().equals(Label.class));
		Assert.assertTrue(comps.get(3).getId().equals("lb4"));
		Assert.assertTrue(comps.get(4).getClass().equals(Label.class));
		Assert.assertTrue(comps.get(4).getId().equals("lb5"));
		Assert.assertTrue(comps.get(5).getClass().equals(Label.class));
		Assert.assertTrue(comps.get(5).getId().equals("lb6"));
	}
	
	@Test
	public void testNonShadowChildThroughShadowRoots() {
		DesktopAgent desktop = connect();
		Component parent = desktop.query("#parent").as(Div.class);
		List<Component> comps = Selectors.find(parent, ":host::shadow > if > if > label");
		Assert.assertTrue("expecting 0, got: " + comps.size(), comps.size() == 0);
	}
	
	@Test
	public void testIdOnlyWithDetachShadow() {
		DesktopAgent desktop = connect();
		Component parent = desktop.query("#parent").as(Div.class);
		List<Component> comps = Selectors.find(parent, "#host::shadow#if1");
		Assert.assertTrue("expecting 0, got: " + comps.size(), comps.size() == 0);
	}
}
