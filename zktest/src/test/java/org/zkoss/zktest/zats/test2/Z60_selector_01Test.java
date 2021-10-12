/* Z60_selector_01Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Apr 23 11:06:16 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class Z60_selector_01Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@button"));
		waitResponse();

		for (JQuery item : jq("@listitem")) {
			Assert.assertEquals("00", item.find("@listcell:last").text());
		}
	}
}
