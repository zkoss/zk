/* B96_ZK_4700Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Mar 18 14:43:17 CST 2021, Created by katherinelin

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B96_ZK_4700Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq("@chosenbox:eq(1)").find(".z-chosenbox-inplace"));
		waitResponse();
		Assertions.assertTrue(jq("@chosenbox:eq(0)").find(".z-chosenbox-item").hasClass("z-chosenbox-hide"));
	}
}
