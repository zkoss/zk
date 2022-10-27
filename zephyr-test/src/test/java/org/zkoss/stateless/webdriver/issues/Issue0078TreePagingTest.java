/* Issue0078TreePagingTest.java

	Purpose:
		
	Description:
		
	History:
		3:11 PM 2021/12/22, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.webdriver.issues;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Iterator;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author jumperchen
 */
public class Issue0078TreePagingTest extends WebDriverTestCase {
	@Test
	public void testTreePaging() {
		connect("/issue0078");
		assertEquals("[ 1 - 20 / 550 ]", jq(".z-paging-info").text());
		assertEquals(" / 28", jq(".z-paging-text").text());

		Iterator<JQuery> iterator = jq(
				".z-treerow .z-treecell-content").iterator();
		String[] result = {
				"1",
					"1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
				"2",
					"1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
		};
		int index = 0;
		while (iterator.hasNext()) {
			assertEquals(result[index++], iterator.next().text());
		}

		click(jq(".z-paging-icon.z-icon-angle-right"));
		waitResponse();
		iterator = jq(
				".z-treerow .z-treecell-content").iterator();
		String[] result2 = {
					"9", "10",
				"3",
					"1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
				"4",
					"1", "2", "3", "4", "5", "6"
		};
		index = 0;
		while (iterator.hasNext()) {
			assertEquals(result2[index++], iterator.next().text());
		}
		assertEquals("[ 21 - 40 / 550 ]", jq(".z-paging-info").text());

		// close 4
		click(jq(".z-icon-caret-down.z-tree-open:eq(1)"));
		waitResponse();

		assertEquals("[ 21 - 40 / 540 ]", jq(".z-paging-info").text());
		assertEquals(" / 27", jq(".z-paging-text").text());

		String[] result3 = {
				"9", "10",
				"3",
				"1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
				"4", // closed
				"5",
					"1", "2", "3", "4", "5"
		};
		index = 0;
		while (iterator.hasNext()) {
			assertEquals(result2[index++], iterator.next().text());
		}

		// open 4
		click(jq(".z-icon-caret-right.z-tree-close"));
		waitResponse();

		index = 0;
		while (iterator.hasNext()) {
			assertEquals(result2[index++], iterator.next().text());
		}
		assertEquals("[ 21 - 40 / 550 ]", jq(".z-paging-info").text());
		assertEquals(" / 28", jq(".z-paging-text").text());
	}
}
