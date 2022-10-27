/* ITreecellRichletTest.java

	Purpose:

	Description:

	History:
		Fri Feb 18 17:57:15 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.webdriver.docs.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.stateless.sul.ITreecell;
import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * A set of unit test for {@link ITreecell} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Data/Tree/Treecell">Treecell</a>,
 * if any.
 *
 * @author katherine
 * @see ITreecell
 */
public class ITreecellRichletTest extends WebDriverTestCase {
	@Test
	public void span() {
		connect("/data/itree/iTreecell/span");
		assertEquals(jq(".z-treecell:eq(1)").width() * 2, jq(".z-treecell:eq(0)").width(), 1);
	}

	@Test
	public void image() {
		connect("/data/itree/iTreecell/image");
		assertTrue(jq(".z-treecell img").attr("src").contains("ZK-Logo.gif"));
		click(jq("@button"));
		waitResponse();
		assertTrue(jq(".z-treecell img").attr("src").contains("ZK-Logo-old.gif"));
	}

	@Test
	public void widthAndHflex() {
		connect("/data/itree/iTreecell/widthAndHflex");
		click(jq("@button:eq(0)"));
		waitResponse();
		assertTrue(hasError());
		assertTrue(jq(".z-label:contains(readonly)").exists());
		click(jq(".z-messagebox-buttons button"));
		waitResponse();
		click(jq("@button:eq(1)"));
		waitResponse();
		assertTrue(hasError());
		assertTrue(jq(".z-label:contains(readonly)").exists());
	}
}