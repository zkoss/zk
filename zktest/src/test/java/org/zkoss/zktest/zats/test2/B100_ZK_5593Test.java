/* B100_ZK_5593Test.java

        Purpose:
                
        Description:
                
        History:
                Thu Dec 14 16:41:14 CST 2023, Created by rebeccalai

Copyright (C) 2023 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B100_ZK_5593Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		assertNoAnyError();
	}
}
