/* B102_ZK_5934Test.java

        Purpose:
                
        Description:
                
        History:
                Mon Apr 28 15:40:29 CST 2025, Created by jamson

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B102_ZK_5934Test extends WebDriverTestCase {

    @Test
    public void test() {
        connect();
        JQuery tb = jq(".z-textbox");
        focus(tb);
        getActions().sendKeys("123").perform();
        blur(tb);
        waitResponse();
        Assertions.assertEquals("server validate: 123", jq("#zk_logbox textarea").val().trim());
    }
}
