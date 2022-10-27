/* ITextboxRichletTest.java

	Purpose:

	Description:

	History:
		Tue Feb 22 11:12:00 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.webdriver.docs.input;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;

import org.zkoss.stateless.sul.ITextbox;
import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * A set of unit test for {@link ITextbox} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Essential_Components/Input/Textbox">Textbox</a>,
 * if any.
 *
 * @author katherine
 * @see ITextbox
 */
public class ITextboxRichletTest extends WebDriverTestCase {
	@Test
	public void rows() {
		connect("/input/itextbox/rows");
		assertEquals("2", jq(".z-textbox").attr("rows"));
		click(jq("@button"));
		waitResponse();
		assertEquals("3", jq(".z-textbox").attr("rows"));
	}

	@Test
	public void submitByEnter() {
		connect("/input/itextbox/submitByEnter");
		getActions().sendKeys(Keys.TAB).sendKeys(Keys.TAB).sendKeys(Keys.ENTER).sendKeys(Keys.TAB).sendKeys(Keys.ENTER).perform();
		waitResponse();
		assertEquals("\n", jq(".z-textbox:eq(0)").val());
		assertEquals("", jq(".z-textbox:eq(1)").val());
		click(jq("@button"));
		waitResponse();
		getActions().sendKeys(Keys.TAB).sendKeys(Keys.ENTER).sendKeys(Keys.TAB).sendKeys(Keys.ENTER).perform();
		waitResponse();
		assertEquals("\n", jq(".z-textbox:eq(1)").val());
	}

	@Test
	public void type() {
		connect("/input/itextbox/type");
		assertEquals("text", jq(".z-textbox:eq(0)").attr("type"));
		assertEquals("password", jq(".z-textbox:eq(1)").attr("type"));
		assertEquals("email", jq(".z-textbox:eq(2)").attr("type"));
		assertEquals("tel", jq(".z-textbox:eq(3)").attr("type"));
		assertEquals("url", jq(".z-textbox:eq(4)").attr("type"));
		click(jq("@button"));
		waitResponse();
		assertEquals("password", jq(".z-textbox:eq(0)").attr("type"));
	}

	@Test
	public void tabbable() {
		connect("/input/itextbox/tabbable");
		Actions act = getActions();
		act.sendKeys(Keys.TAB).sendKeys(Keys.TAB).sendKeys(Keys.TAB).sendKeys(Keys.TAB).perform();
		waitResponse();
		assertEquals("", jq(".z-textbox:eq(0)").val());
		assertEquals("\t", jq(".z-textbox:eq(1)").val());
		click(jq("@button"));
		waitResponse();
		act.sendKeys(Keys.TAB).sendKeys(Keys.TAB).sendKeys(Keys.ARROW_RIGHT).sendKeys(Keys.TAB).perform();
		assertEquals("\t", jq(".z-textbox:eq(1)").val());
	}

	@Test
	public void constraint() {
		connect("/input/itextbox/constraint");
		click(jq(".z-textbox"));
		click(jq("@button"));
		waitResponse();
		assertTrue(jq(".z-errorbox:contains(Please enter an e-mail address)").exists());
		click(jq(".z-textbox"));
		click(jq("@button"));
		waitResponse();
		assertTrue(jq(".z-errorbox-open").isVisible());
		assertFalse(jq(".z-errorbox:contains(Please enter an e-mail address)").exists());
	}
}