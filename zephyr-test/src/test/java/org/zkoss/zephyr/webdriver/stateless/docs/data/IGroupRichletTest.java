/* IGroupRichletTest.java

	Purpose:

	Description:

	History:
		Mon Apr 11 14:07:48 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.stateless.docs.data;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * A set of unit test for {@link org.zkoss.statelessex.sul.IGroup} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Data/Grid/Group">Group</a>,
 * if any.
 *
 * @author katherine
 * @see org.zkoss.statelessex.sul.IGroup
 */
public class IGroupRichletTest extends WebDriverTestCase {
	@Test
	public void label() {
		connect("/data/iGroup/label");
		assertTrue(jq(".z-group").text().contains("group"));
		click(jq("@button"));
		waitResponse();
		assertTrue(jq(".z-group").text().contains(" new group"));
	}

	@Test
	public void open() {
		connect("/data/iGroup/open");
		assertFalse(jq(".z-group").hasClass("z-group-open"));
		click(jq("@button"));
		waitResponse();
		assertTrue(jq(".z-group").hasClass("z-group-open"));
	}
}