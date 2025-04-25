/* B102_ZK_5614Test.java

        Purpose:
                
        Description:
                
        History:
                Thu Apr 24 18:14:18 CST 2025, Created by jamson

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B102_ZK_5614Test extends WebDriverTestCase {
    
    @Test
    public void test() {
        connect();
        JQuery x = jq(".z-datebox-input");
        focus(x);
        getActions().sendKeys("1967-05-01 00:00:00").perform();
        blur(x);
        waitResponse();
        Assertions.assertEquals("Mon May 01 00:00:00 PST 1967", jq("$day").text());
    }
}
