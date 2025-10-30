/* B86_ZK_4197Test.java

		Purpose:
                
		Description:
                
		History:
				Mon Jan 21 15:32:53 CST 2019, Created by charlesqiu

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B86_ZK_4197Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq(".z-groupbox-title"));
		waitResponse();
		Assertions.assertEquals("animationspeed: 0", getZKLog());
	}
}
