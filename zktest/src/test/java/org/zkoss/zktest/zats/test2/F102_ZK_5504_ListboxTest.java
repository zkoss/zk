/* B100_ZK_5468_ListboxTest.java

	Purpose:
		
	Description:
		
	History:
		2:30 PM 2023/7/21, Created by jumperchen

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
public class F102_ZK_5504_ListboxTest extends WebDriverTestCase {

	@Test
	public void testInsertBefore() {
		connect();

		String[] result = {"0aaa0", "1aaa1", "2aaa2"};
		for (Iterator<JQuery> it = jq(".z-listbox").iterator(); it.hasNext(); ) {
			int index = 0;
			JQuery listbox = it.next();
			for (Iterator<JQuery> itemsIt = listbox.find(".z-listitem").iterator(); itemsIt.hasNext(); ) {
				JQuery item = itemsIt.next();
				assertEquals(result[index++], item.text());
			}
		}

		click(jq("button:contains(insert before)"));
		waitResponse();


		// for ZK-5504 behavior on without ROD Case
		result = new String[]{"0ddd", "0aaa0", "1aaa1", "2aaa2"};
		for (Iterator<JQuery> it = jq(".z-listbox").iterator(); it.hasNext(); ) {
			int index = 0;
			JQuery listbox = it.next();
			for (Iterator<JQuery> itemsIt = listbox.find(".z-listitem").iterator(); itemsIt.hasNext(); ) {
				JQuery item = itemsIt.next();
				if (index == 0) {
					assertThat(item.text(), startsWith(result[index++]));
					continue;
				}
				assertEquals(result[index++], item.text());
			}
			result = new String[]{"0ddd", "1aaa0", "2aaa1", "3aaa2"};
		}

		click(jq("button:contains(remove)"));
		waitResponse(true);

		result = new String[] {"0aaa0", "1aaa1", "2aaa2"};
		for (Iterator<JQuery> it = jq(".z-listbox").iterator(); it.hasNext(); ) {
			int index = 0;
			JQuery listbox = it.next();
			for (Iterator<JQuery> itemsIt = listbox.find(".z-listitem").iterator(); itemsIt.hasNext(); ) {
				JQuery item = itemsIt.next();
				assertEquals(result[index++], item.text());
			}
		}
	}
}