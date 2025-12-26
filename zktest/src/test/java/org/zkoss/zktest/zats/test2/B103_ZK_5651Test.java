/* B103_ZK_5651Test.java

        Purpose:
                
        Description:
                
        History:
                Wed Dec 24 09:56:16 CST 2025, Created by peakerlee

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B103_ZK_5651Test extends WebDriverTestCase {
    @Test
    public void test() {
        connect();
        waitResponse();
        type(jq("$tb"), "test");

        sendKeys(jq("$tb"), Keys.ENTER);
        waitResponse();

        String log = getZKLog();
        Assertions.assertTrue(log.contains("onOK"));
        Assertions.assertTrue(log.contains("onChange"));
        assertNoJSError();
    }
}
