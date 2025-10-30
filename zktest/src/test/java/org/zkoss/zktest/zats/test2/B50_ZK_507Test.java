/* B50_ZK_507Test.java

		Purpose:
		
		Description:
		
		History:
				Tue Apr 09 16:56:53 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B50_ZK_507Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq(".z-column"));
		waitResponse();
		click(jq(".z-paging-next"));
		waitResponse();
		Assertions.assertEquals("name05", jq(".z-row").eq(0).text());
	}
}
