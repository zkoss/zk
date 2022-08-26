/* F85_ZK_3689Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Aug 04 16:14:48 CST 2017, Created by rudyhuang

Copyright (C) 2017 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class F85_ZK_3689Test extends WebDriverTestCase {
	@Test
	public void test() throws Exception {
		connect();
		sleep(2000);
		Assertions.assertEquals("", jq("$zkver").val());
		Assertions.assertNotEquals(jq("$build").text(), jq("$zkbuild").val());
		Assertions.assertTrue(jq("$ckezver").val().matches("^[a-z0-9]+$"),
				"The module version info exposed"
				);
	}
}
