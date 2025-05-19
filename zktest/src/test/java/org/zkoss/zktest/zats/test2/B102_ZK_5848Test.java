/* B102_ZK_5848Test.java

        Purpose:
                
        Description:
                
        History:
                Thu May 15 18:10:37 CST 2025, Created by jamson

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B102_ZK_5848Test extends WebDriverTestCase {

    @Test
    public void test() {
        connect();
        focus(jq("@textbox"));
        selectAll();
        for (int i = 0; i < 4; ++i) {
            getActions().sendKeys(Keys.TAB).perform();
            paste();
        }
        Assertions.assertEquals("918", jq("@intbox").val());
        Assertions.assertEquals("918", jq("@doublebox").val());
        Assertions.assertEquals("918", jq("@longbox").val());
        Assertions.assertEquals("918", jq("@decimalbox").val());
    }
}
