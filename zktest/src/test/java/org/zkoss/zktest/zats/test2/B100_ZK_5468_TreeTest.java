/* B100_ZK_5468_TreeTest.java

	Purpose:
		
	Description:
		
	History:
		2:48 PM 2023/7/21, Created by jumperchen

Copyright (C) 2023 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Iterator;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author jumperchen
 */
public class B100_ZK_5468_TreeTest extends WebDriverTestCase {

	@Test
	public void testInsertBefore() {
		connect();

		String[] result = {"0aaa0", "1aaa1", "2aaa2"};
		for (Iterator<JQuery> it = jq(".z-tree").iterator(); it.hasNext(); ) {
			int index = 0;
			JQuery tree = it.next();
			for (Iterator<JQuery> rowsIt = tree.find(".z-treerow").iterator(); rowsIt.hasNext(); ) {
				JQuery row = rowsIt.next();
				assertEquals(result[index++], row.text());
			}
		}

		click(jq("button:contains(insert before)"));
		waitResponse();

		result = new String[]{"0ddd", "1aaa0", "2aaa1", "3aaa2"};
		for (Iterator<JQuery> it = jq(".z-tree").iterator(); it.hasNext(); ) {
			int index = 0;
			JQuery tree = it.next();
			for (Iterator<JQuery> rowsIt = tree.find(".z-treerow").iterator(); rowsIt.hasNext(); ) {
				JQuery row = rowsIt.next();
				if (index == 0) {
					assertThat(row.text(), startsWith(result[index++]));
					continue;
				}
				assertEquals(result[index++], row.text());
			}
		}

		click(jq("button:contains(remove)"));
		waitResponse(true);
		result = new String[] {"0aaa0", "1aaa1", "2aaa2"};
		for (Iterator<JQuery> it = jq(".z-tree").iterator(); it.hasNext(); ) {
			int index = 0;
			JQuery tree = it.next();
			for (Iterator<JQuery> rowsIt = tree.find(".z-treerow").iterator(); rowsIt.hasNext(); ) {
				JQuery row = rowsIt.next();
				assertEquals(result[index++], row.text());
			}
		}
	}
}