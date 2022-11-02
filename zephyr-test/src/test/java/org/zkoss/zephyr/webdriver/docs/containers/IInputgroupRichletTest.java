/* IInputgroupRichletTest.java

	Purpose:

	Description:

	History:
		Mon Apr 18 17:14:37 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.docs.containers;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.stateless.sul.IInputgroup;
import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * A set of unit test for {@link IInputgroup} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Containers/Inputgroup">Inputgroup</a>,
 * if any.
 *
 * @author katherine
 * @see IInputgroup
 */
public class IInputgroupRichletTest extends WebDriverTestCase {
	@Test
	public void orient() {
		connect("/containers/iinputgroup/orient");
		assertTrue(jq(".z-inputgroup-vertical").exists());
		click(jq("@button"));
		waitResponse();
		assertFalse(jq(".z-inputgroup-vertical").exists());
	}
}