/* F86_ZK_4023Test.java

        Purpose:
                
        Description:
                
        History:
                Fri Aug 10 15:27:59 CST 2018, Created by klyve

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

public class F86_ZK_4023Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery datebox1 = jq("$d1 > input");
		testDateboxInput(datebox1, "Jan 0, 2018", "Dec 31, 2017");
		testDateboxInput(datebox1, "Feb 30, 2018", "Mar 2, 2018");
		testDateboxInput(datebox1, "Dec 20, 2018", "Dec 20, 2018");

		JQuery datebox2 = jq("$d2 > input");
		testDateboxInput(datebox2, "Jan 0, 2018", "Jan 0, 2018", true);
		testDateboxInput(datebox2, "Feb 30, 2018", "Feb 30, 2018", true);
		testDateboxInput(datebox2, "Dec 20, 2018", "Dec 20, 2018");
	}

	private void testDateboxInput(JQuery datebox, String input, String expected) {
		testDateboxInput(datebox, input, expected, false);
	}

	private void testDateboxInput(JQuery datebox, String input, String expected, boolean hasError) {
		try {
			type(datebox, input);
			waitResponse();
			Assert.assertEquals(expected, datebox.val());
			if (hasError)
				Assert.assertTrue(jq(".z-errorbox").exists());
		} finally {
			type(datebox, ""); // make empty to close the error pop
			waitResponse();
		}
	}
}
