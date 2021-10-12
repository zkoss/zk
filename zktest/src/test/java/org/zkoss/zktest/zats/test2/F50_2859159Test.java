/* F50_2859159Test.java

	Purpose:
		
	Description:
		
	History:
		Mon Apr 08 16:20:55 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class F50_2859159Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@button:eq(0)"));
		waitResponse();
		Assert.assertEquals("req added: req=rvalue", jq("@label:last").text());

		click(jq("@button:eq(1)"));
		waitResponse();
		Assert.assertEquals("sess added: sess=svalue", jq("@label:last").text());

		click(jq("@button:eq(2)"));
		waitResponse();
		Assert.assertEquals("app added: app=avalue", jq("@label:last").text());
	}
}
