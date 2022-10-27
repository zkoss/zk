/* IDoublespinnerRichletTest.java

	Purpose:

	Description:

	History:
		Wed Mar 02 15:55:46 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.webdriver.docs.input;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.stateless.sul.IDoublespinner;
import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * A set of unit test for {@link IDoublespinner} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Input/Doublespinner">Doublespinner</a>,
 * if any.
 *
 * @author katherine
 * @see IDoublespinner
 */
public class IDoublespinnerRichletTest extends WebDriverTestCase {
	@Test
	public void buttonVisible() {
		connect("/input/iDoublespinner/buttonVisible");
		assertTrue(jq(".z-doublespinner-disabled").exists());
		click(jq("@button"));
		waitResponse();
		assertFalse(jq(".z-doublespinner-disabled").exists());
	}

	@Test
	public void step() {
		connect("/input/iDoublespinner/step");
		JQuery up = jq(".z-doublespinner-up");
		click(up);
		waitResponse();
		assertEquals("0.2", jq(".z-doublespinner-input").val());
		click(jq("@button"));
		waitResponse();
		click(up);
		waitResponse();
		assertEquals("0.3", jq(".z-doublespinner-input").val());
	}

	@Test
	public void value() {
		connect("/input/iDoublespinner/value");
		assertEquals("0.1", jq(".z-doublespinner-input").val());
		click(jq("@button"));
		waitResponse();
		assertEquals("1.0", jq(".z-doublespinner-input").val());
	}
}