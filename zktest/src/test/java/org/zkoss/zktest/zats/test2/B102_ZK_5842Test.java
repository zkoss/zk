/* B102_ZK_5842Test.java

        Purpose:
                
        Description:
                
        History:
                Fri May 16 16:09:44 CST 2025, Created by jamson

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import java.util.Collections;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeOptions;


import org.zkoss.test.webdriver.WebDriverTestCase;

public class B102_ZK_5842Test extends WebDriverTestCase {
    @Override
    protected ChromeOptions getWebDriverOptions() {
        return super.getWebDriverOptions()
                .setExperimentalOption("mobileEmulation", Collections.singletonMap("deviceName", "iPad"));
    }
    @Test
    public void test() {
        connect();
        Assertions.assertEquals("none", jq(".z-grid-body").css("scrollbar-width"));
    }
}
