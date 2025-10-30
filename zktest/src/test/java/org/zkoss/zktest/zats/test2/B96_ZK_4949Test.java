/* B96_ZK_4949Test.java

		Purpose:
		
		Description:
		
		History:
				Tue Jul 13 18:07:15 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B96_ZK_4949Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq("$lb"));
		waitResponse();
		click(jq("$btn"));
		waitResponse();
		assertNoJSError();
	}
}
