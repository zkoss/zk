/* B102_ZK_5752Test.java

        Purpose:
                
        Description:
                
        History:
                Tue May 06 11:01:23 CST 2025, Created by jamson

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B102_ZK_5752Test extends WebDriverTestCase {

    @Test
    public void test() {
        connect();
        sendKeys(jq("input[type=file]"), System.getProperty("user.dir") + "/src/main/webapp/test2/B101_ZK_5793.png");

        waitResponse();

        Assertions.assertEquals("test _onload() error", jq(".z-messagebox .z-label").text());
    }
}
