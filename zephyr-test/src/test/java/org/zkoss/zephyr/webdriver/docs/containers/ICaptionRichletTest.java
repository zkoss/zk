/* ICaptionRichletTest.java

	Purpose:

	Description:

	History:
		Thu Mar 10 16:03:36 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.docs.containers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.stateless.zpr.ICaption;
import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * A set of unit test for {@link ICaption} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Containers/Caption">Caption</a>,
 * if any.
 *
 * @author katherine
 * @see ICaption
 */
public class ICaptionRichletTest extends WebDriverTestCase {
	@Test
	public void label() {
		connect("/containers/ICaption/label");
		assertEquals("label", jq(".z-caption").text());
		click(jq("@button"));
		waitResponse();
		assertEquals("new label", jq(".z-caption").text());
	}
}