/* B102_ZK_5616_1Test.java

        Purpose:
                
        Description:
                
        History:
                Mon May 19 16:43:46 CST 2025, Created by jamson

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import java.util.Collections;

import org.openqa.selenium.chrome.ChromeOptions;

public class B102_ZK_5616_1Test extends B102_ZK_5616Test {
    @Override
    protected ChromeOptions getWebDriverOptions() {
        return super.getWebDriverOptions()
                .setExperimentalOption("mobileEmulation", Collections.singletonMap("deviceName", "iPad"));
    }
}
