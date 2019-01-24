/* F85_ZK_3329Test.java

	Purpose:
		
	Description:
		
	History:
		Wed May 16 16:11:42 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.ClientWidget;
import org.zkoss.zktest.zats.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class F85_ZK_3329Test extends WebDriverTestCase {
	@Test
	public void testSlidable() {
		connect();

		waitResponse(true);
		JQuery regionBar = jq("$east .z-east-collapsed");
		JQuery slide = jq("$east.z-east-slide .z-east");
		JQuery btn = jq("$east .z-borderlayout-icon:visible");
		JQuery slideToggle = jq("@button:contains(east toggle slide)");
		JQuery slidableToggle = jq("@checkbox:contains(east slidable) > input");

		clickAndWait(regionBar);
		assertFalse("The slide should be hidden", slide.isVisible());
		clickAndWait(btn);

		clickAndWait(slideToggle);
		assertTrue("The slide should be shown", slide.isVisible());
		clickAndWait(slideToggle);
		assertFalse("The slide should be hidden", slide.isVisible());

		clickAndWait(slidableToggle);
		clickAndWait(regionBar);
		assertTrue("The slide should be shown", slide.isVisible());
	}

	private void clickAndWait(ClientWidget cw) {
		click(cw);
		waitResponse();
	}

	@Test
	public void testClosable() {
		connect();

		JQuery regionBar = jq("$west .z-west-collapsed");
		JQuery slide = jq("$west.z-west-slide .z-west");
		JQuery real = jq("$west .z-west");
		JQuery btn = jq("$west .z-borderlayout-icon:visible");
		JQuery openToggle = jq("@button:contains(west toggle open)");
		JQuery closableToggle = jq("@checkbox:contains(west closable) > input");
		JQuery splitterBtn = jq("$west .z-west-splitter-button");

		assertFalse("The open button exists", btn.exists());

		clickAndWait(regionBar);
		assertTrue("The slide should be shown", slide.isVisible());
		clickAndWait(regionBar);
		assertFalse("The slide should be hidden", slide.isVisible());

		clickAndWait(openToggle);
		assertTrue("The region should be opened", real.isVisible());
		assertFalse("The close button exists", btn.exists());

		clickAndWait(openToggle);
		assertFalse("The region should be closed", real.isVisible());

		clickAndWait(closableToggle);
		clickAndWait(btn);
		assertTrue("The region should be opened", real.isVisible());

		clickAndWait(closableToggle);
		assertFalse("The close button exists", btn.exists());
		assertTrue("The splitter button should be disabled",
				splitterBtn.hasClass("z-west-splitter-button-disabled"));

		clickAndWait(splitterBtn);
		assertTrue("The region should remain opened", real.isVisible());

		clickAndWait(closableToggle);
		assertTrue("The close button should exist", btn.exists());
		assertFalse("The splitter button should be enabled",
				splitterBtn.hasClass("z-west-splitter-button-disabled"));
	}

	@Test
	public void testCollapsibleAndClosableRelations() {
		connect();

		JQuery btn = jq("$west .z-borderlayout-icon:visible");
		JQuery collapsible = jq("@checkbox:contains(west collapsible) > input");
		JQuery closable = jq("@checkbox:contains(west closable) > input");
		JQuery openToggle = jq("@button:contains(west toggle open)");

		// Test collapsible = true, closable switch
		// close
		clickAndWait(closable); // true
		shouldHaveShown(btn);
		clickAndWait(closable); // false
		shouldHaveHidden(btn);
		// open
		clickAndWait(openToggle);
		clickAndWait(closable); // true
		shouldHaveShown(btn);
		clickAndWait(closable); // false
		shouldHaveHidden(btn);

		// Test closable = false, collapsible switch
		// Expected always hidden because closable = false
		clickAndWait(collapsible); // false
		shouldHaveHidden(btn);
		clickAndWait(collapsible); // true
		shouldHaveHidden(btn);
		// Test closable = true, collapsible switch
		clickAndWait(closable);
		clickAndWait(collapsible); // false
		shouldHaveHidden(btn);
		clickAndWait(collapsible); // true
		shouldHaveShown(btn);
	}

	private void shouldHaveShown(JQuery btn) {
		assertTrue("button should be visible", btn.isVisible());
	}

	private void shouldHaveHidden(JQuery btn) {
		assertFalse("button should be invisible", btn.isVisible());
	}
}
