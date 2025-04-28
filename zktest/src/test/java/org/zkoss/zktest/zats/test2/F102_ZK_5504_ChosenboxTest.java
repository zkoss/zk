/* F102_ZK_5504_ChosenboxTest.java

	Purpose:
		
	Description:
		
	History:
		05:22 PM 2025/4/17, Created by jumperchen

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
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
public class F102_ZK_5504_ChosenboxTest extends WebDriverTestCase {
	@Test
	public void testInsertBefore() {
		connect();

		focus(jq(".z-chosenbox-input"));
		waitResponse(true);

		String[] result = {"0 - aaa0", "1 - aaa1", "2 - aaa2"};
		int index = 0;
		for (Iterator<JQuery> it = jq(".z-chosenbox-option").iterator(); it.hasNext(); ) {
			JQuery jQuery = it.next();
			assertEquals(result[index++], jQuery.text());
		}

		click(jq("button:contains(insert before)"));
		waitResponse();

		focus(jq(".z-chosenbox-input"));
		waitResponse(true);

		result = new String[]{"0 - ddd", "0 - aaa0", "1 - aaa1", "2 - aaa2"};
		index = 0;
		for (Iterator<JQuery> it = jq(".z-chosenbox-option").iterator(); it.hasNext(); ) {
			JQuery jQuery = it.next();
			if (index == 0) {
				assertThat(jQuery.text(), startsWith(result[index++]));
				continue;
			}
			assertEquals(result[index++], jQuery.text());
		}

		click(jq("button:contains(remove)"));
		waitResponse(true);
		focus(jq(".z-chosenbox-input"));
		waitResponse(true);

		result = new String[]{"0 - aaa0", "1 - aaa1", "2 - aaa2"};
		index = 0;
		for (Iterator<JQuery> it = jq(".z-chosenbox-option").iterator(); it.hasNext(); ) {
			JQuery jQuery = it.next();
			assertEquals(result[index++], jQuery.text());
		}
	}
}
