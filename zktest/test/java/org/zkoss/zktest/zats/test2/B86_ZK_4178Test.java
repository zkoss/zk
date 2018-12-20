/* B86_ZK_4178Test.java

	Purpose:

	Description:

	History:
		Thu Dec 20 12:49:13 CST 2018, Created by rudyhuang

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
public class B86_ZK_4178Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery datebox1 = jq("$d1 > input");
		testDateboxInput(datebox1, "Aug 32, 2018", "Aug 32, 2018", true);

		JQuery datebox2 = jq("$d2 > input");
		testDateboxInput(datebox2, "Aug 32, 2018", "Aug 32, 2018", true);
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
