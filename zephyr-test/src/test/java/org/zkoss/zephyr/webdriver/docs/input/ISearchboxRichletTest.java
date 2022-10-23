/* ISearchboxRichletTest.java

	Purpose:

	Description:

	History:
		Tue Mar 08 14:40:59 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.docs.input;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * A set of unit test for {@link org.zkoss.zephyrex.zpr.ISearchbox} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Input/Searchbox">Searchbox</a>,
 * if any.
 *
 * @author katherine
 * @see org.zkoss.zephyrex.zpr.ISearchbox
 */
public class ISearchboxRichletTest extends WebDriverTestCase {
	@Test
	public void autoclose() {
		connect("/input/iSearchbox/autoclose");
		click(jq(".z-searchbox"));
		waitResponse();
		click(jq(".z-searchbox-item"));
		waitResponse();
		assertFalse(jq(".z-searchbox-popup").isVisible());
		click(jq("@button"));
		waitResponse();
		click(jq(".z-searchbox"));
		waitResponse();
		click(jq(".z-searchbox-item"));
		assertTrue(jq(".z-searchbox-popup").isVisible());
	}

	@Test
	public void multiple() {
		connect("/input/iSearchbox/multiple");
		assertTrue(jq(".z-searchbox-item-check").exists());
		click(jq("@button"));
		waitResponse();
		assertFalse(jq(".z-searchbox-item-check").exists());
	}

	@Test
	public void open() {
		connect("/input/iSearchbox/open");
		assertTrue(jq(".z-searchbox-popup").isVisible());
		click(jq("@button"));
		waitResponse();
		assertFalse(jq(".z-searchbox-popup").isVisible());
	}

	@Test
	public void placeholder() {
		connect("/input/iSearchbox/placeholder");
		assertEquals("search..", jq(".z-searchbox-placeholder").text());
		click(jq("@button"));
		waitResponse();
		assertEquals("search", jq(".z-searchbox-placeholder").text());
	}

	@Test
	public void searchMessage() {
		connect("/input/iSearchbox/searchMessage");
		assertEquals("search", jq(".z-searchbox-search").attr("placeholder"));
		click(jq("@button"));
		waitResponse();
		assertEquals("doSearch", jq(".z-searchbox-search").attr("placeholder"));
	}
}