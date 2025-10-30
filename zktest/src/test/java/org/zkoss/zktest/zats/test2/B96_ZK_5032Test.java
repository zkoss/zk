/* B96_ZK_5032Test.java

	Purpose:
		
	Description:
		
	History:
		3:29 PM 2021/11/16, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author jumperchen
 */
public class B96_ZK_5032Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		Assertions.assertEquals("myString", jq("@label:eq(1)").text());
		Assertions.assertEquals("myString", jq("@label:eq(3)").text());
		Assertions.assertEquals("myString", jq("@label:eq(5)").text());
	}
}