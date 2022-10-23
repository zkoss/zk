/* IButtonRichletTest.java

	Purpose:

	Description:

	History:
		Wed Mar 30 15:39:29 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.docs.essential_components;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * A set of unit test for {@link org.zkoss.zephyr.zpr.IButton} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Essential_Components/Button">Button</a>,
 * if any.
 *
 * @author katherine
 * @see org.zkoss.zephyr.zpr.IButton
 */
public class IButtonRichletTest extends WebDriverTestCase {
	@Test
	public void autodisable() {
		connect("/essential_components/iButton/autodisable");
		assertEquals("self", WebDriverTestCase.getEval("zk.$('$btn')._autodisable"));
		click(jq("@button:eq(1)"));
		waitResponse();
		assertEquals("null", WebDriverTestCase.getEval("zk.$('$btn')._autodisable"));
	}

	@Test
	public void dir() {
		connect("/essential_components/iButton/dir");
		assertEquals("reverse", WebDriverTestCase.getEval("zk.$('$btn')._dir"));
		click(jq("@button:eq(1)"));
		waitResponse();
		assertEquals("normal", WebDriverTestCase.getEval("zk.$('$btn')._dir"));
	}

	@Test
	public void disabled() {
		connect("/essential_components/iButton/disabled");
		assertEquals("disabled", jq(".z-button").attr("disabled"));
		click(jq("@button:eq(1)"));
		waitResponse();
		assertEquals("null", jq(".z-button").attr("disabled"));
	}

	@Test
	public void href() {
		connect("/essential_components/iButton/href");
		assertEquals("www.google.com", WebDriverTestCase.getEval("zk.$('$btn')._href"));
		click(jq("@button:eq(1)"));
		waitResponse();
		assertEquals("www.yahoo.com", WebDriverTestCase.getEval("zk.$('$btn')._href"));
	}

	@Test
	public void target() {
		connect("/essential_components/iButton/target");
		assertEquals("go", WebDriverTestCase.getEval("zk.$('$btn')._target"));
		click(jq("@button:eq(1)"));
		waitResponse();
		assertEquals("go2", WebDriverTestCase.getEval("zk.$('$btn')._target"));
	}

	@Test
	public void type() {
		connect("/essential_components/iButton/type");
		assertEquals("submit", WebDriverTestCase.getEval("zk.$('$btn')._type"));
		click(jq("@button:eq(1)"));
		waitResponse();
		assertEquals("button", WebDriverTestCase.getEval("zk.$('$btn')._type"));
	}

	@Test
	public void upload() {
		connect("/essential_components/iButton/upload");
		assertEquals("multiple", jq(".z-upload input").attr("multiple"));
		click(jq("@button:eq(1)"));
		waitResponse();
		assertEquals("null", jq(".z-upload input").attr("multiple"));
	}

	@Test
	public void orient() {
		connect("/essential_components/iButton/orient");
		int width = jq("@button:eq(0)").width();
		click(jq("@button:eq(1)"));
		waitResponse();
		assertTrue(width < jq("@button:eq(0)").width());
	}
}