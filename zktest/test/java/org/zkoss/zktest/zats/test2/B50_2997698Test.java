/* B50_2997698Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Mar 22 12:31:48 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B50_2997698Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		Assert.assertEquals(jq("@label:eq(0)").text(), jq("@decimalbox:eq(0)").val());
		Assert.assertEquals(jq("@label:eq(1)").text(), jq("@decimalbox:eq(1)").val());
		Assert.assertEquals(jq("@label:eq(2)").text(), jq("@decimalbox:eq(2)").val());
	}
}
