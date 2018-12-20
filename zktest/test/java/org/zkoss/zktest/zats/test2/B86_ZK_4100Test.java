/* B86_ZK_4100Test.java

	Purpose:

	Description:

	History:
		Wed Dec 19 18:33:21 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class B86_ZK_4100Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		String input = "31.11.2018";
		testDateboxInput(jq("@datebox:eq(0) > input"), input, input, true);
		testDateboxInput(jq("@datebox:eq(1) > input"), input, input, true);
		testDateboxInput(jq("@datebox:eq(2) > input"), input, "01.12.2018");
		testDateboxInput(jq("@datebox:eq(3) > input"), input, input, true);
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
