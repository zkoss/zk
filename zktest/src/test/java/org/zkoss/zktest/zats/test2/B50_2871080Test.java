/* B50_2871080Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Mar 15 14:46:45 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B50_2871080Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		Assertions.assertEquals("t[\u00A0]", jq("@separator ~ span:eq(0)").text()); // \u00A0=&nbsp;
		Assertions.assertEquals("l[&nbsp;]", jq("@separator ~ span:eq(1)").text());
	}
}
