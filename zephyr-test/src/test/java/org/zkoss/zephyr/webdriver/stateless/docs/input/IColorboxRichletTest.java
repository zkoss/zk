/* IColorboxRichletTest.java

	Purpose:

	Description:

	History:
		Tue Mar 01 18:13:59 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.stateless.docs.input;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * A set of unit test for {@link org.zkoss.statelessex.sul.IColorbox} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Input/Colorbox">Colorbox</a>,
 * if any.
 *
 * @author katherine
 * @see org.zkoss.statelessex.sul.IColorbox
 */
public class IColorboxRichletTest extends WebDriverTestCase {
	@Test
	public void color() {
		connect("/input/iColorbox/color");
		assertTrue(jq(".z-colorbox-current").attr("style").contains("background-color: rgb(255, 255, 255)"));
		click(jq("@button"));
		waitResponse();
		assertTrue(jq(".z-colorbox-current").attr("style").contains("background-color: rgb(170, 170, 220)"));
	}

	@Test
	public void disabled() {
		connect("/input/iColorbox/disabled");
		assertTrue(jq(".z-colorbox.z-colorbox-disabled").exists());
		click(jq("@button"));
		waitResponse();
		assertFalse(jq(".z-colorbox.z-colorbox-disabled").exists());
	}

	@Test
	public void value() {
		connect("/input/iColorbox/value");
		assertTrue(jq(".z-colorbox-current").attr("style").contains("background-color: rgb(255, 255, 0)"));
	}
}