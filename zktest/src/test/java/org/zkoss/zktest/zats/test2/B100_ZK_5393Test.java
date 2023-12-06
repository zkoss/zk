/* B100_ZK_5393Test.java

        Purpose:
                
        Description:
                
        History:
                Fri Dec 01 17:33:57 CST 2023, Created by rebeccalai

Copyright (C) 2023 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B100_ZK_5393Test extends WebDriverTestCase {
	@Test
	public void test() throws Exception {
		connect();
		waitResponse();
		dropUploadFile(jq("@dropupload"), Paths.get("src/main/webapp/test2/img/sun.jpg"));
		waitResponse();
		assertNoAnyError();
		assertEquals("sun.jpg", getZKLog());
	}
}
