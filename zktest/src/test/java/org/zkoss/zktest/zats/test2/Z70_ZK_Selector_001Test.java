/* Z70_ZK_Selector_001Test.java

		Purpose:
		
		Description:
		
		History:
				Fri Jun 21 18:33:22 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class Z70_ZK_Selector_001Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		
		click(jq("@button:contains(Test Button)"));
		waitResponse(true);
		
		Assertions.assertEquals("OK", jq(".z-label").eq(1).text().trim());
	}
}
