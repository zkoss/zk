/* Issue0099TreeRichletTest.java

	Purpose:

	Description:

	History:
		3:39 PM 2022/2/11, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.stateless.issues;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author jumperchen
 */
public class Issue0099TreeRichletTest extends WebDriverTestCase {
	@Test
	public void issue0099() {
		connect("/stateless/issue0099/createOnOpen");
		assertEquals("Intel Snares XML", jq(".z-treecell-text").text());
		assertTrue(jq(".z-tree-icon > .z-tree-close").exists());
		click(jq(".z-tree-icon > .z-tree-close"));
		waitResponse();
		assertTrue(jq(".z-treecell-text:contains(New added)").exists());
		assertTrue(jq(".z-tree-icon > .z-tree-open").exists());
	}
}
