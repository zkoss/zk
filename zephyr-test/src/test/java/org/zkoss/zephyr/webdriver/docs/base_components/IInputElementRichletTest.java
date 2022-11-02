/* IInputElementRichletTest.java

	Purpose:

	Description:

	History:
		Tue Feb 22 14:19:37 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.docs.base_components;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;

import org.zkoss.stateless.sul.IInputElement;
import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * A set of unit test for {@link IInputElement} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Base_Components/InputElement">InputElement</a>,
 * if any.
 *
 * @author katherine
 * @see IInputElement
 */
public class IInputElementRichletTest extends WebDriverTestCase {
	@Test
	public void multipleConstraints() {
		connect("/base_components/iinputelement/multipleConstraints");
		Actions action = getActions();
		action.sendKeys(Keys.TAB).sendKeys(Keys.TAB).sendKeys(Keys.TAB).sendKeys("1").sendKeys(Keys.TAB).perform();
		waitResponse();
		assertEquals(3, jq(".z-errorbox").length());
		click(jq(".z-errorbox-close:eq(2)"));
		waitResponse();
		action.sendKeys(Keys.BACK_SPACE).sendKeys("-1").sendKeys(Keys.TAB).perform();
		waitResponse();
		assertEquals(3, jq(".z-errorbox").length());
	}

	@Test
	public void i18nErrorMessage() {
		connect("/base_components/iinputelement/i18nErrorMessage");
		getActions().sendKeys(Keys.TAB).sendKeys(Keys.TAB).perform();
		waitResponse();
		assertEquals(1, jq(".z-errorbox").length());
	}

	@Test
	public void customConstraint() {
		connect("/base_components/iinputelement/customConstraint");
		getActions().sendKeys(Keys.TAB).sendKeys("1").sendKeys(Keys.TAB).perform();
		waitResponse();
		assertEquals(1, jq(".z-errorbox").length());
	}

	@Test
	public void value() {
		connect("/base_components/iinputelement/value");
		assertEquals("test", jq("input:eq(0)").val());
		assertEquals(LocalDate.of(2000, 1, 1).toString(), jq("input:eq(1)").val());
		assertEquals("1", jq("input:eq(2)").val());
	}

	@Test
	public void disabled() {
		connect("/base_components/iinputelement/disabled");
		assertEquals(3, jq("input:disabled").length());
	}

	@Test
	public void errorbox() {
		connect("/base_components/iinputelement/errorbox");
		getActions().sendKeys(Keys.TAB).sendKeys(Keys.TAB).sendKeys(Keys.TAB).sendKeys(Keys.TAB).perform();
		waitResponse();
		assertTrue(jq(".z-errorbox:eq(0) .z-icon-home").exists());
		assertTrue(jq(".z-errorbox:eq(1) .z-icon-home").exists());
		assertTrue(jq(".z-errorbox:eq(2) .z-icon-home").exists());
		assertTrue(jq(".z-errorbox:eq(0)").hasClass("customErrorStyle"));
		assertTrue(jq(".z-errorbox:eq(1)").hasClass("customErrorStyle"));
		assertTrue(jq(".z-errorbox:eq(2)").hasClass("customErrorStyle"));
	}

	@Test
	public void inplace() {
		connect("/base_components/iinputelement/inplace");
		assertTrue(jq(".z-textbox").hasClass("z-textbox-inplace"));
		assertTrue(jq(".z-datebox").hasClass("z-datebox-inplace"));
		assertTrue(jq(".z-intbox").hasClass("z-intbox-inplace"));
	}

	@Test
	public void attributes() {
		connect("/base_components/iinputelement/attributes");
		JQuery textbox = jq("input:eq(0)");
		JQuery datebox = jq("input:eq(1)");
		JQuery intbox = jq("input:eq(2)");
		assertEquals("disabled", textbox.attr("disabled"));
		assertEquals("disabled", datebox.attr("disabled"));
		assertEquals("disabled", intbox.attr("disabled"));
		assertEquals("1", textbox.attr("size"));
		assertEquals("1", datebox.attr("size"));
		assertEquals("1", intbox.attr("size"));
		assertEquals("3", textbox.attr("maxlength"));
		assertEquals("3", datebox.attr("maxlength"));
		assertEquals("3", intbox.attr("maxlength"));
		assertEquals("textbox", textbox.attr("name"));
		assertEquals("datebox", datebox.attr("name"));
		assertEquals("intbox", intbox.attr("name"));
		assertEquals("type some thing here", textbox.attr("placeholder"));
		assertEquals("type some thing here", datebox.attr("placeholder"));
		assertEquals("type some thing here", intbox.attr("placeholder"));
	}

	@Test
	public void instant() {
		connect("/base_components/iinputelement/instant");
		Actions action = getActions();
		click(jq("input:eq(0)"));
		action.sendKeys("1").perform();
		waitResponse();
		assertEquals("doChange", getZKLog());
		closeZKLog();
		click(jq("input:eq(1)"));
		action.sendKeys("1").perform();
		waitResponse();
		assertEquals("doChange", getZKLog());
		closeZKLog();
		click(jq("input:eq(2)"));
		action.sendKeys("1").perform();
		waitResponse();
		assertEquals("doChange", getZKLog());
		closeZKLog();
		click(jq("input:eq(3)"));
		action.sendKeys("1").perform();
		waitResponse();
		assertFalse(jq("#zk_log").exists());
		action.sendKeys(Keys.TAB).perform();
		waitResponse();
		closeZKLog();
		click(jq("input:eq(4)"));
		action.sendKeys("1").perform();
		waitResponse();
		assertFalse(jq("#zk_log").exists());
		action.sendKeys(Keys.TAB).perform();
		waitResponse();
		closeZKLog();
		click(jq("input:eq(5)"));
		action.sendKeys("1").perform();
		waitResponse();
		assertFalse(jq("#zk_log").exists());
	}
}