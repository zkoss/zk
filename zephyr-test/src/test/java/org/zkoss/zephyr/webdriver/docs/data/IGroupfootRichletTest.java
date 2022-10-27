/* IGroupfootRichletTest.java

	Purpose:

	Description:

	History:
		Mon Apr 11 14:07:35 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.docs.data;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * A set of unit test for {@link org.zkoss.statelessex.zpr.IGroupfoot} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Data/Grid/Groupfoot">Groupfoot</a>,
 * if any.
 *
 * @author katherine
 * @see org.zkoss.statelessex.zpr.IGroupfoot
 */
public class IGroupfootRichletTest extends WebDriverTestCase {
	@Test
	public void label() {
		connect("/data/iGroupfoot/label");
		assertEquals("foot", jq(".z-groupfoot").text());
		click(jq("@button"));
		waitResponse();
		assertEquals("new foot", jq(".z-groupfoot").text());
	}
}