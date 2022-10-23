/* IMenuitemRichletTest.java

	Purpose:

	Description:

	History:
		Tue Apr 12 11:41:32 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.docs.essential_components;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * A set of unit test for {@link org.zkoss.zephyr.zpr.IMenuitem} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Essential_Components/Menu/Menuitem">Menuitem</a>,
 * if any.
 *
 * @author katherine
 * @see org.zkoss.zephyr.zpr.IMenuitem
 */
public class IMenuitemRichletTest extends WebDriverTestCase {
	@Test
	public void autocheck() {
		connect("/essential_components/iMenuitem/autocheck");
		checkItem();
		assertTrue(jq(".z-menuitem-checked").exists());
		click(jq("@button"));
		waitResponse();
		checkItem();
		assertTrue(jq(".z-menuitem-checked").exists());
	}

	@Test
	public void autodisable() {
		connect("/essential_components/iMenuitem/autodisable");
		assertEquals("btn2", WebDriverTestCase.getEval("zk.$('$item')._autodisable"));
		click(jq("@button:eq(1)"));
		waitResponse();
		assertEquals("btn2", WebDriverTestCase.getEval("zk.$('$item')._autodisable"));
	}

	@Test
	public void checked() {
		connect("/essential_components/iMenuitem/checked");
		open();
		assertTrue(jq(".z-menuitem-checked").exists());
		click(jq("@button"));
		waitResponse();
		assertFalse(jq(".z-menuitem-checked").exists());
	}

	@Test
	public void href() {
		connect("/essential_components/iMenuitem/href");
		open();
		assertTrue(jq(".z-menuitem-content").attr("href").contains("google"));
		click(jq("@button"));
		waitResponse();
		assertTrue(jq(".z-menuitem-content").attr("href").contains("yahoo"));
	}

	@Test
	public void target() {
		connect("/essential_components/iMenuitem/target");
		open();
		assertTrue(jq(".z-menuitem-content").attr("target").contains("target1"));
		click(jq("@button"));
		waitResponse();
		assertTrue(jq(".z-menuitem-content").attr("target").contains("target2"));
	}

	private void open() {
		click(jq(".z-menu-icon"));
		waitResponse();
	}

	private void checkItem() {
		open();
		click(jq(".z-menupopup-content"));
		waitResponse();
	}
}