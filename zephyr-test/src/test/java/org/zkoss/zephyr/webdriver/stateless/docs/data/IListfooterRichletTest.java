/* IListfooterRichletTest.java

	Purpose:

	Description:

	History:
		Fri Apr 08 18:04:14 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.stateless.docs.data;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.stateless.sul.IListfooter;
import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * A set of unit test for {@link IListfooter} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Data/Listbox/Listfooter">Listfooter</a>,
 * if any.
 *
 * @author katherine
 * @see IListfooter
 */
public class IListfooterRichletTest extends WebDriverTestCase {

	@Test
	public void image() {
		connect("/data/iList/iListfooter/image");
		assertTrue(jq(".z-listfooter img").attr("src").contains("ZK-Logo.gif"));
		click(jq("@button"));
		waitResponse();
		assertTrue(jq(".z-listfooter img").attr("src").contains("ZK-Logo-old.gif"));
	}
}