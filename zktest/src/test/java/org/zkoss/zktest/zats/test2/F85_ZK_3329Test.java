/* F85_ZK_3329Test.java

	Purpose:
		
	Description:
		
	History:
		Wed May 16 16:11:42 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.ClientWidget;
import org.zkoss.test.webdriver.ztl.JQuery;

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
		assertFalse(slide.isVisible(), "The slide should be hidden");
		clickAndWait(btn);

		clickAndWait(slideToggle);
		assertTrue(slide.isVisible(), "The slide should be shown");
		clickAndWait(slideToggle);
		assertFalse(slide.isVisible(), "The slide should be hidden");

		clickAndWait(slidableToggle);
		clickAndWait(regionBar);
		assertTrue(slide.isVisible(), "The slide should be shown");
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

		assertFalse(btn.exists(), "The open button exists");

		clickAndWait(regionBar);
		assertTrue(slide.isVisible(), "The slide should be shown");
		clickAndWait(regionBar);
		assertFalse(slide.isVisible(), "The slide should be hidden");

		clickAndWait(openToggle);
		assertTrue(real.isVisible(), "The region should be opened");
		assertFalse(btn.exists(), "The close button exists");

		clickAndWait(openToggle);
		assertFalse(real.isVisible(), "The region should be closed");

		clickAndWait(closableToggle);
		clickAndWait(btn);
		assertTrue(real.isVisible(), "The region should be opened");

		clickAndWait(closableToggle);
		assertFalse(btn.exists(), "The close button exists");
		assertTrue(splitterBtn.hasClass("z-west-splitter-button-disabled"),
				"The splitter button should be disabled");

		clickAndWait(splitterBtn);
		assertTrue(real.isVisible(), "The region should remain opened");

		clickAndWait(closableToggle);
		assertTrue(btn.exists(), "The close button should exist");
		assertFalse(splitterBtn.hasClass("z-west-splitter-button-disabled"),
				"The splitter button should be enabled");
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
		assertTrue(btn.isVisible(), "button should be visible");
	}

	private void shouldHaveHidden(JQuery btn) {
		assertFalse(btn.isVisible(), "button should be invisible");
	}
}
