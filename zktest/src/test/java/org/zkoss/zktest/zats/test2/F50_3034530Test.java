/* F50_3034530Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Apr 09 12:22:16 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class F50_3034530Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		Assertions.assertEquals("center", jq("@grid:eq(0) @footer:first").css("text-align"));
		Assertions.assertEquals("center", jq("@grid:eq(1) @groupfoot @cell:first").css("text-align"));
		Assertions.assertEquals("center", jq("@listbox:eq(0) @listfooter:first").css("text-align"));
		Assertions.assertEquals("center", jq("@tree:eq(0) @treefooter:first").css("text-align"));
	}
}
