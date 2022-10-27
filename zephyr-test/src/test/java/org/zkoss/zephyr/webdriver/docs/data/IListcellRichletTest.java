/* IListcellRichletTest.java

	Purpose:

	Description:

	History:
		Thu Apr 07 17:01:26 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.docs.data;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.stateless.zpr.IListcell;
import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * A set of unit test for {@link IListcell} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Data/Listbox/Listcell">Listcell</a>,
 * if any.
 *
 * @author katherine
 * @see IListcell
 */
public class IListcellRichletTest extends WebDriverTestCase {
	@Test
	public void span() {
		connect("/data/iListcell/span");
		assertEquals("2", jq(".z-listcell").attr("colspan"));
		click(jq("@button"));
		waitResponse();
		assertEquals("1", jq(".z-listcell").attr("colspan"));
	}
}