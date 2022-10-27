/* ILabelElementRichletTest.java

	Purpose:

	Description:

	History:
		Thu Mar 10 16:43:53 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.docs.base_components;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.interactions.Actions;

import org.zkoss.stateless.zpr.ILabelElement;
import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * A set of unit test for {@link ILabelElement} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Base_Components/LabelElement">ILabelElement</a>,
 * if any.
 *
 * @author katherine
 * @see ILabelElement
 */
public class ILabelImageElementRichletTest extends WebDriverTestCase {
	@Test
	public void hoverImage() {
		connect("/base_components/iLabelImageElement/hoverImage");
		assertEquals("/zephyr-test/zephyr/ZK-Logo_en_US.gif", jq(".z-button-image").attr("src"));
		assertEquals("/zephyr-test/zephyr/ZK-Logo_en_US.gif", jq(".z-caption-image").attr("src"));
		assertEquals("/zephyr-test/zephyr/ZK-Logo_en_US.gif", jq(".z-checkbox-content img").attr("src"));
		assertEquals("/zephyr-test/zephyr/ZK-Logo_en_US.gif", jq(".z-comboitem-image img").attr("src"));
		Actions act = getActions();
		// button
		// mouse in
		act.moveToElement(toElement(jq(".z-button-image"))).build().perform();
		waitResponse();
		assertEquals("/zephyr-test/zephyr/ZK-Logo.gif", jq(".z-button-image").attr("src"));
		// mouse out
		act.moveToElement(toElement(jq(".z-button:eq(1)"))).build().perform();
		waitResponse();
		assertEquals("/zephyr-test/zephyr/ZK-Logo_en_US.gif", jq(".z-button-image").attr("src"));
		// caption
		// mouse in
		act.moveToElement(toElement(jq(".z-caption-image"))).build().perform();
		waitResponse();
		assertEquals("/zephyr-test/zephyr/ZK-Logo.gif", jq(".z-caption-image").attr("src"));
		// mouse out
		act.moveToElement(toElement(jq(".z-button:eq(1)"))).build().perform();
		waitResponse();
		assertEquals("/zephyr-test/zephyr/ZK-Logo_en_US.gif", jq(".z-caption-image").attr("src"));
		// checkbox
		// mouse in
		act.moveToElement(toElement(jq(".z-checkbox"))).build().perform();
		waitResponse();
		assertEquals("/zephyr-test/zephyr/ZK-Logo.gif", jq(".z-checkbox img").attr("src"));
		// mouse out
		act.moveToElement(toElement(jq(".z-button:eq(1)"))).build().perform();
		waitResponse();
		assertEquals("/zephyr-test/zephyr/ZK-Logo_en_US.gif", jq(".z-checkbox img").attr("src"));
		// comboitem
		// mouse in
		act.moveToElement(toElement(jq(".z-comboitem-image"))).build().perform();
		waitResponse();
		assertEquals("/zephyr-test/zephyr/ZK-Logo.gif", jq(".z-comboitem img").attr("src"));
		// mouse out
		act.moveToElement(toElement(jq(".z-button:eq(1)"))).build().perform();
		waitResponse();
		assertEquals("/zephyr-test/zephyr/ZK-Logo_en_US.gif", jq(".z-comboitem img").attr("src"));

		//change hoverImage
		click(jq("@button:eq(1)"));
		waitResponse();
		// button
		act.moveToElement(toElement(jq(".z-button-image"))).build().perform();
		waitResponse();
		assertEquals("/zephyr-test/zephyr/ZK-Logo-old.gif", jq(".z-button-image").attr("src"));
		// caption
		act.moveToElement(toElement(jq(".z-caption"))).build().perform();
		waitResponse();
		assertEquals("/zephyr-test/zephyr/ZK-Logo-old.gif", jq(".z-caption-image").attr("src"));
		// checkbox
		act.moveToElement(toElement(jq(".z-checkbox"))).build().perform();
		waitResponse();
		assertEquals("/zephyr-test/zephyr/ZK-Logo-old.gif", jq(".z-checkbox img").attr("src"));
		// comboitem
		click(jq(".z-combobox-button"));
		waitResponse();
		act.moveToElement(toElement(jq(".z-comboitem"))).build().perform();
		waitResponse();
		assertEquals("/zephyr-test/zephyr/ZK-Logo-old.gif", jq(".z-comboitem img").attr("src"));
	}

	@Test
	public void iconSclass() {
		connect("/base_components/iLabelImageElement/iconSclass");
		assertTrue(jq(".z-button i").hasClass("z-icon-home"));
		assertTrue(jq(".z-caption-content i").hasClass("z-icon-home"));
		assertTrue(jq(".z-checkbox-content i").hasClass("z-icon-home"));
		assertTrue(jq(".z-comboitem i").hasClass("z-icon-home"));
		click(jq("@button:eq(1)"));
		waitResponse();
		assertTrue(jq(".z-button i").hasClass("z-icon-user"));
		assertTrue(jq(".z-caption-content i").hasClass("z-icon-user"));
		assertTrue(jq(".z-checkbox-content i").hasClass("z-icon-user"));
		assertTrue(jq(".z-comboitem i").hasClass("z-icon-user"));
	}

	@Test
	public void preloadImage() {
		connect("/base_components/iLabelImageElement/preloadImage");
		assertEquals("true", WebDriverTestCase.getEval("zk.$('$btn')._preloadImage"));
		assertEquals("true", WebDriverTestCase.getEval("zk.$('$caption')._preloadImage"));
		assertEquals("true", WebDriverTestCase.getEval("zk.$('$cb')._preloadImage"));
		assertEquals("true", WebDriverTestCase.getEval("zk.$('$ci')._preloadImage"));
	}
}