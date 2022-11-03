/* IListitemRichletTest.java

	Purpose:

	Description:

	History:
		Thu Apr 07 17:01:35 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.stateless.docs.data;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.stateless.sul.IListitem;
import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * A set of unit test for {@link IListitem} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Data/Listbox/Listitem">Listitem</a>,
 * if any.
 *
 * @author katherine
 * @see IListitem
 */
public class IListitemRichletTest extends WebDriverTestCase {
	@Test
	public void label() {
		connect("/data/iListitem/label");
		assertTrue(jq(".z-listcell").text().contains("item"));
		click(jq("@button"));
		waitResponse();
		assertTrue(jq(".z-listcell").text().contains("new item"));
	}
}