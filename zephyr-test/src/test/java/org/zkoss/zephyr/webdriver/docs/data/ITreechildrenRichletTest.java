/* ITreechildrenRichletTest.java

	Purpose:

	Description:

	History:
		Mon Feb 21 14:14:31 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.docs.data;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.stateless.zpr.ITreechildren;
import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * A set of unit test for {@link ITreechildren} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Data/Tree/Treechildren">Treechildren</a>,
 * if any.
 *
 * @author katherine
 * @see ITreechildren
 */
public class ITreechildrenRichletTest extends WebDriverTestCase {
	@Test
	public void widthAndHflex() {
		connect("/data/itree/iTreechildren/widthAndHflex");
		click(jq("@button:eq(0)"));
		waitResponse();
		assertTrue(hasError());
		assertTrue(jq(".z-label:contains(Not allowed to set width)").exists());
		click(jq(".z-messagebox-buttons button"));
		waitResponse();
		click(jq("@button:eq(1)"));
		waitResponse();
		assertTrue(hasError());
		assertTrue(jq(".z-label:contains(Not allowed to set hflex)").exists());
	}
}