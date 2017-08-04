/* F85_ZK_3689Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Aug 04 16:14:48 CST 2017, Created by rudyhuang

Copyright (C) 2017 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class F85_ZK_3689Test extends WebDriverTestCase {
	@Test
	public void test() throws Exception {
		connect();

		Assert.assertEquals("", jq("$zkver").val());
		Assert.assertEquals("", jq("$zkbuild").val());
		Assert.assertTrue(
			"The module version info exposed",
			jq("$ckezver").val().matches("^[a-z0-9]+$")
		);
	}
}
