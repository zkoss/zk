/* ICombobuttonRichletTest.java

	Purpose:

	Description:

	History:
		Tue Apr 19 17:29:05 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.webdriver.docs.essential_components;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.stateless.sul.ICombobutton;
import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * A set of unit test for {@link ICombobutton} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Essential_Components/Combobutton">Combobutton</a>,
 * if any.
 *
 * @author katherine
 * @see ICombobutton
 */
public class ICombobuttonRichletTest extends WebDriverTestCase {
	@Test
	public void autodrop() {
		connect("/essential_components/iCombobutton/autodrop");
		getActions().moveToElement(toElement(jq(".z-combobutton"))).build().perform();
		assertTrue(jq(".z-popup").isVisible());
		click(jq("@button"));
		waitResponse();
		getActions().moveToElement(toElement(jq(".z-combobutton"))).build().perform();
		assertFalse(jq(".z-popup").isVisible());
		click(jq(".z-combobutton"));
		assertTrue(jq(".z-popup").isVisible());
	}
}