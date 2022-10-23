/* IChosenboxRichletTest.java

	Purpose:

	Description:

	History:
		Thu Feb 24 16:15:45 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.docs.input;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * A set of unit test for {@link org.zkoss.zephyrex.zpr.IChosenbox} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Input/Chosenbox">Chosenbox</a>,
 * if any.
 *
 * @author katherine
 * @see org.zkoss.zephyrex.zpr.IChosenbox
 */
public class IChosenboxRichletTest extends WebDriverTestCase {
	@Test
	public void creatable() {
		connect("/input/iChosenbox/creatable");
		Actions action = getActions();
		action.sendKeys(Keys.TAB).sendKeys(Keys.TAB).sendKeys("a").perform();
		waitResponse();
		assertTrue(jq(".z-chosenbox-empty-creatable").isVisible());
		action.sendKeys(Keys.TAB).sendKeys("a").perform();
		waitResponse();
		assertFalse(jq(".z-chosenbox-empty-creatable").isVisible());
		click(jq("@button"));
		waitResponse();
		action.sendKeys(Keys.TAB).sendKeys(Keys.TAB).sendKeys("a").perform();
		waitResponse();
		assertTrue(jq(".z-chosenbox-empty-creatable").isVisible());
	}

	@Test
	public void createMessage() {
		connect("/input/iChosenbox/createMessage");
		getActions().sendKeys(Keys.TAB).sendKeys(Keys.TAB).sendKeys("a").perform();
		waitResponse();
		assertEquals("Create new item", jq(".z-chosenbox-empty-creatable span").text());
		click(jq("@button"));
		waitResponse();
		getActions().sendKeys(Keys.TAB).sendKeys("a").perform();
		waitResponse(true);
		assertEquals("create", jq(".z-chosenbox-empty-creatable span").text());

	}

	@Test
	public void disabled() {
		connect("/input/iChosenbox/disabled");
		assertFalse(jq(".z-chosenbox-disabled").exists());
		click(jq("@button"));
		waitResponse();
		assertTrue(jq(".z-chosenbox-disabled").exists());
	}

	@Test
	public void emptyMessage() {
		connect("/input/iChosenbox/emptyMessage");
		assertEquals("empty", jq(".z-chosenbox-input").val());
		click(jq("@button"));
		waitResponse();
		assertEquals("empty2", jq(".z-chosenbox-input").val());
	}

	@Test
	public void inplace() {
		connect("/input/iChosenbox/inplace");
		Actions action = getActions();
		click(jq(".z-chosenbox-inplace:eq(0)"));
		waitResponse();
		action.sendKeys("i").perform();
		waitResponse();
		click(jq(".z-chosenbox-option"));
		waitResponse();
		click(jq(".z-chosenbox-inplace:eq(0)"));
		waitResponse();
		action.sendKeys("i").perform();
		waitResponse();
		click(jq(".z-chosenbox-option:eq(1)"));
		waitResponse();
		assertEquals("item 0, item 1", jq(".z-chosenbox-inplace:eq(0)").text());
		click(jq("$cb"));
		action.sendKeys("i").perform();
		waitResponse();
		click(jq(".z-chosenbox-popup:eq(1) .z-chosenbox-option"));
		waitResponse();
		click(jq("$cb"));
		waitResponse();
		action.sendKeys("i").perform();
		waitResponse();
		click(jq(".z-chosenbox-popup:eq(1) .z-chosenbox-option:eq(1)"));
		waitResponse();
		assertEquals(2, jq(".z-chosenbox:eq(1) .z-chosenbox-item").length());
	}

	@Test
	public void name() {
		connect("/input/iChosenbox/name");
		String name = jq(".z-chosenbox").toWidget().get("name");
		assertEquals("this is chosenbox", name);
		click(jq("@button"));
		waitResponse();
		name = jq(".z-chosenbox").toWidget().get("name");
		assertEquals("chosenbox", name);
	}

	@Test
	public void noResultsText() {
		connect("/input/iChosenbox/noResultsText");
		click(jq(".z-chosenbox"));
		waitResponse();
		assertEquals("No such item", jq(".z-chosenbox-empty").text());
	}

	@Test
	public void open() {
		connect("/input/iChosenbox/open");
		JQuery box1 = jq(".z-chosenbox-popup:eq(0)");
		assertFalse(box1.isVisible());
		assertTrue(box1.offsetLeft() < jq(".z-chosenbox-popup:eq(0)").width());
		assertTrue(jq(".z-chosenbox-popup:eq(1)").isVisible());
	}

	@Test
	public void separator() {
		connect("/input/iChosenbox/separator");
		Actions action = getActions();
		click(jq(".z-chosenbox-input:eq(0)"));
		waitResponse();
		action.sendKeys("i").perform();
		waitResponse();
		action.sendKeys("1").perform();
		waitResponse();
		assertEquals("item 0", jq(".z-chosenbox-item-content").text());
		click(jq(".z-chosenbox:eq(1)"));
		waitResponse();
		action.sendKeys("i").perform();
		waitResponse();
		action.sendKeys("1").perform();
		waitResponse();
		assertEquals("i1", jq(".z-chosenbox-textcontent").text());
		click(jq("@button"));
		waitResponse();
	}
}