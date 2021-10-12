/* B90_ZK_4411Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Nov 21 11:26:36 CST 2019, Created by jameschu

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author jameschu
 */
public class B90_ZK_4412Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		Assert.assertEquals(jq("$m1").children("span").offsetTop(), jq("$m2").children("span").offsetTop());
		Assert.assertEquals(jq("$b1").children("span").offsetTop(), jq("$b2").children("span").offsetTop());
	}
}
