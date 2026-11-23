/* B86_ZK_4289Test.java

	Purpose:
		
	Description:
		
	History:
		Tue May 07 12:43:29 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B86_ZK_4289Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@button:contains(kill desktop but recoverable)"));
		waitResponse();

		type(jq("@textbox"), "Tom");
		waitResponse();

		click(jq("@button:contains(Enter)"));
		waitResponse();

		Assert.assertEquals("Hi, Tom", getMessageBoxContent());
	}
}
