/* ILabelRichletTest.java

	Purpose:

	Description:

	History:
		Wed Apr 06 16:04:03 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.webdriver.docs.essential_components;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.stateless.sul.ILabel;
import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * A set of unit test for {@link ILabel} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Essential_Components/Label">Label</a>,
 * if any.
 *
 * @author katherine
 * @see ILabel
 */
public class ILabelRichletTest extends WebDriverTestCase {
	@Test
	public void maxlength() {
		connect("/essential_components/iLabel/maxlength");
		assertEquals("123...", jq(".z-label").text());
		click(jq("@button"));
		waitResponse();
		assertEquals("12345...", jq(".z-label").text());
	}

	@Test
	public void multiline() {
		connect("/essential_components/iLabel/multiline");
		int height = jq(".z-label").height();
		click(jq("@button"));
		waitResponse();
		assertTrue(height > jq(".z-label").height());
	}

	@Test
	public void pre() {
		connect("/essential_components/iLabel/pre");
		int width = jq(".z-label").width();
		click(jq("@button"));
		waitResponse();
		assertTrue(width > jq(".z-label").width());
	}

	@Test
	public void value() {
		connect("/essential_components/iLabel/value");
		assertEquals("test", jq(".z-label").text());
		click(jq("@button"));
		waitResponse();
		assertEquals("test2", jq(".z-label").text());
	}
}