/* Issue0083Test.java

	Purpose:
		
	Description:
		
	History:
		3:26 PM 2021/12/13, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.webdriver.issues;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Iterator;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author jumperchen
 */
public class Issue0083Test extends WebDriverTestCase {

	@Test
	public void testTreecolSort() {
		connect("/issues/issue_0083.sul");
		assertFalse(jq(".z-icon-caret-down").exists());
		Iterator<JQuery> iterator = jq(".z-treechildren tr").iterator();
		iterator.hasNext();
		assertEquals("Visible ROOT", iterator.next().text());
		for (int i = 0; i < 10 && iterator.hasNext(); i++) {
			assertEquals(String.valueOf(i), iterator.next().text());
		}

		// test ascending
		clickAt(jq(".z-treecol-content"), 5, 5);
		waitResponse();
		assertTrue(jq(".z-icon-caret-up").exists());
		iterator = jq(".z-treechildren tr").iterator();
		for (int i = 0; i < 10 && iterator.hasNext(); i++) {
			assertEquals(String.valueOf(i), iterator.next().text());
		}
		iterator.hasNext();
		assertEquals("Visible ROOT", iterator.next().text());

		// test descending
		clickAt(jq(".z-treecol-content"), 5, 5);
		waitResponse();
		assertTrue(jq(".z-icon-caret-down").exists());
		iterator = jq(".z-treechildren tr").iterator();
		iterator.hasNext();
		assertEquals("Visible ROOT", iterator.next().text());
		for (int i = 10; --i > 0 && iterator.hasNext();) {
			assertEquals(String.valueOf(i), iterator.next().text());
		}
	}
}
