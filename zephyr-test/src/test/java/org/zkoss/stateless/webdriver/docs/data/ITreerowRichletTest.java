/* ITreerowRichletTest.java

	Purpose:

	Description:

	History:
		Fri Feb 18 18:32:26 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.webdriver.docs.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.stateless.sul.ITreerow;
import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * A set of unit test for {@link ITreerow} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Data/Tree/Treerow">Treerow</a>,
 * if any.
 *
 * @author katherine
 * @see ITreerow
 */
public class ITreerowRichletTest extends WebDriverTestCase {
	@Test
	public void image() {
		connect("/data/itree/iTreerow/image");
		assertTrue(jq(".z-treerow img").exists());
	}

	@Test
	public void label() {
		connect("/data/itree/iTreerow/label");
		assertEquals("item 1", jq(".z-treecell-content").text());
	}

	@Test
	public void widthAndHflex() {
		connect("/data/itree/iTreerow/widthAndHflex");
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