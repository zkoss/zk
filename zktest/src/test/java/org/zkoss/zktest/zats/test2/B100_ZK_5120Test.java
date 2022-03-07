/* B100_ZK_5120Test.java

	Purpose:

	Description:

	History:
		Fri Mar 04 11:15:17 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author katherine
 */
public class B100_ZK_5120Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq("@button"));
		waitResponse();
		type(jq(".z-combobox-input"), "a");
		waitResponse();
		Assert.assertEquals("No suggestions", jq(".z-combobox-empty-search-message").text());
	}
}