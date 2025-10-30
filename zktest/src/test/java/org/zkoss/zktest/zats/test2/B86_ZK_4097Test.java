/* B86_ZK_4097Test.java

        Purpose:
                
        Description:
                
        History:
                Thu Nov 08 10:38:13 CST 2018, Created by leon

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B86_ZK_4097Test  extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		Assertions.assertFalse(isZKLogAvailable());
	}
}
