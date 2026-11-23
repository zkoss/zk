/* B90_ZK_4465Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Dec 20 14:49:55 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B90_ZK_4465Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@button"));
		waitResponse(true);

		Assert.assertEquals(
			jq("@drawer @div").outerHeight(),
			jq(widget("@drawer").$n("cave")).height(),
			2
		);
	}
}
