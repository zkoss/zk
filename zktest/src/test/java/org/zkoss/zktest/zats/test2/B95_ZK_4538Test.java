/* B95_ZK_4538Test.java

		Purpose:
		
		Description:
		
		History:
				Tue Aug 18 14:31:34 CST 2020, Created by leon

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B95_ZK_4538Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq("@button"));
		waitResponse();
		assertNoJSError();
	}
}
