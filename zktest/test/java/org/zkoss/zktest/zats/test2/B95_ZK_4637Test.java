/* B95_ZK_4637Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Aug 07 16:43:30 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class B95_ZK_4637Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery searchbox = jq("@searchbox");
		String previousBackground = searchbox.css("background-color");

		getActions().clickAndHold(toElement(searchbox)).perform();
		Assert.assertEquals(previousBackground, searchbox.css("background-color"));
	}
}
