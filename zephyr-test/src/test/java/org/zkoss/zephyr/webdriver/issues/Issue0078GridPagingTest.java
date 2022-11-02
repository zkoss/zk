/* Issue0078GridPagingTest.java

	Purpose:
		
	Description:
		
	History:
		Thu Jan 20 16:30:15 CST 2021, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.issues;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Iterator;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author katherine
 */
public class Issue0078GridPagingTest extends WebDriverTestCase {
	@Test
	public void testGridPaging() {
		connect("/issue0078/grid");
		assertEquals("[ 1 - 5 / 52 ]", jq(".z-paging-info").text());
		assertEquals(" / 11", jq(".z-paging-text").text());

		// first page
		Iterator<JQuery> iterator = jq(".z-rows .z-row-content").iterator();
		String[] result = {
				"1", "2", "3", "4", "5"
		};
		int index = 0;
		while (iterator.hasNext()) {
			assertEquals(result[index++], iterator.next().text());
		}

		// second page
		click(jq(".z-paging-icon.z-icon-angle-right"));
		waitResponse();
		assertEquals("[ 6 - 10 / 52 ]", jq(".z-paging-info").text());
		assertEquals("2", jq(".z-paging-input").val());
		iterator = jq(".z-rows .z-row-content").iterator();
		String[] result2 = {
				"6", "7", "8", "9", "10"
		};
		index = 0;
		while (iterator.hasNext()) {
			assertEquals(result2[index++], iterator.next().text());
		}

		// last page
		click(jq(".z-paging-icon.z-icon-angle-double-right"));
		waitResponse();
		assertEquals("[ 51 - 52 / 52 ]", jq(".z-paging-info").text());
		assertEquals("11", jq(".z-paging-input").val());
		iterator = jq(".z-rows .z-row-content").iterator();
		String[] result3 = {
				"51", "52"
		};
		index = 0;
		while (iterator.hasNext()) {
			assertEquals(result3[index++], iterator.next().text());
		}
	}
}
