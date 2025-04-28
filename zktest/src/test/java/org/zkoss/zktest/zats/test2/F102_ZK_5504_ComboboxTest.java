/* B100_ZK_5468_ComboboxTest.java

	Purpose:
		
	Description:
		
	History:
		12:31 PM 2023/7/21, Created by jumperchen

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
public class F102_ZK_5504_ComboboxTest extends WebDriverTestCase {
	@Test
	public void testInsertBefore() {
		connect();

		click(jq(".z-combobox-button"));
		waitResponse(true);

		String[] result = {"0 - aaa0", "1 - aaa1", "2 - aaa2"};
		int index = 0;
		for (Iterator<JQuery> it = jq(".z-comboitem-text").iterator(); it.hasNext(); ) {
			JQuery jQuery = it.next();
			assertEquals(result[index++], jQuery.text().replace(" ", " "));
		}

		click(jq("button:contains(insert before)"));
		waitResponse();

		click(jq(".z-combobox-button"));
		waitResponse(true);

		result = new String[]{"0 - ddd", "0 - aaa0", "1 - aaa1", "2 - aaa2"};
		index = 0;
		for (Iterator<JQuery> it = jq(".z-comboitem-text").iterator(); it.hasNext(); ) {
			JQuery jQuery = it.next();
			if (index == 0) {
				assertThat(jQuery.text().replace(" ", " "), startsWith(result[index++]));
				continue;
			}
			assertEquals(result[index++], jQuery.text().replace(" ", " "));
		}

		click(jq("button:contains(remove)"));
		waitResponse(true);
		click(jq(".z-combobox-button"));
		waitResponse(true);
		result = new String[]{"0 - aaa0", "1 - aaa1", "2 - aaa2"};
		index = 0;
		for (Iterator<JQuery> it = jq(".z-comboitem-text").iterator(); it.hasNext(); ) {
			JQuery jQuery = it.next();
			assertEquals(result[index++], jQuery.text().replace(" ", " "));
		}
	}
}
