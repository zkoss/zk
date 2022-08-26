/* B96_ZK_5040Test.java

	Purpose:
		
	Description:
		
	History:
		4:39 PM 2021/11/15, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Iterator;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author jumperchen
 */
public class B96_ZK_5040Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq(".z-treecol-checkable"));
		waitResponse();

		assertTrue(jq(".z-treecol-checkable").hasClass("z-treecol-checked"));
		Iterator<JQuery> iterator = jq(".z-treerow").iterator();
		int count = 0;
		while (iterator.hasNext()) {
			assertTrue(iterator.next().hasClass("z-treerow-selected"));
			count++;
		}
		assertEquals(5, count);

		click(jq(".z-paging-icon.z-icon-angle-right"));
		waitResponse();

		// second page
		iterator = jq(".z-treerow").iterator();
		count = 0;
		while (iterator.hasNext()) {
			assertTrue(iterator.next().hasClass("z-treerow-selected"));
			count++;
		}
		assertEquals(5, count);

		// first page
		click(jq(".z-paging-icon.z-icon-angle-left"));
		waitResponse();

		iterator = jq(".z-treerow").iterator();
		count = 0;
		while (iterator.hasNext()) {
			assertTrue(iterator.next().hasClass("z-treerow-selected"));
			count++;
		}
		assertEquals(5, count);
	}
}
