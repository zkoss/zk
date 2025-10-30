/* B100_ZK_5468_BiglistboxTest.java

	Purpose:
		
	Description:
		
	History:
		5:53 PM 2023/7/20, Created by jumperchen

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
public class B100_ZK_5468_BiglistboxTest extends WebDriverTestCase {
	@Test
	public void testInsertBefore() {
		connect();
		String[] result = {"0 - aaa0", "1 - aaa1", "2 - aaa2"};
		int index = 0;
		for (Iterator<JQuery> it = jq(".z-biglistbox-row").iterator(); it.hasNext(); ) {
			JQuery jQuery = it.next();
			assertEquals(result[index++], jQuery.find(".flash-initially").text());
		}

		click(jq("button:contains(insert before)"));
		waitResponse();
		result = new String[]{"0 - ddd", "1 - aaa0", "2 - aaa1", "3 - aaa2"};
		index = 0;
		for (Iterator<JQuery> it = jq(".z-biglistbox-row").iterator(); it.hasNext(); ) {
			JQuery jQuery = it.next();
			if (index == 0) {
				assertThat(jQuery.find(".flash-initially").text(), startsWith(result[index++]));
				continue;
			}
			assertEquals(result[index++], jQuery.find(".flash-initially").text());
		}

		click(jq("button:contains(remove)"));
		waitResponse();
		result = new String[]{"0 - aaa0", "1 - aaa1", "2 - aaa2"};
		index = 0;
		for (Iterator<JQuery> it = jq(".z-biglistbox-row").iterator(); it.hasNext(); ) {
			JQuery jQuery = it.next();
			assertEquals(result[index++], jQuery.find(".flash-initially").text());
		}
	}
}
