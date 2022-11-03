/* ITreecolRichletTest.java

	Purpose:

	Description:

	History:
		Mon Feb 21 14:41:04 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.stateless.docs.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.stateless.sul.ITreecol;
import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * A set of unit test for {@link ITreecol} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Data/Tree/Treecol">Treecol</a>,
 * if any.
 *
 * @author katherine
 * @see ITreecol
 */
public class ITreecolRichletTest extends WebDriverTestCase {
	@Test
	public void maxlength() {
		connect("/data/itree/iTreecol/maxlength");
		assertTrue(jq(".z-treecol-content").text().length() < "tree column".length());
	}

	@Test
	public void sortAscendingAndDescending() {
		connect("/data/itree/iTreecol/sortAscendingAndDescending");
		assertNotEquals("item a", jq(".z-treecell-text:eq(0)").text());
		click(jq(".z-treecol"));
		waitResponse();
		assertEquals("item a", jq(".z-treecell-text:eq(0)").text());
		click(jq(".z-treecol"));
		waitResponse();
		assertEquals("item f", jq(".z-treecell-text:eq(0)").text());
	}

	@Test
	public void sortDirection() {
		connect("/data/itree/iTreecol/sortDirection");
		assertEquals("item f", jq(".z-treecell-text:eq(0)").text());
	}
}