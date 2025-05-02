/* F102_ZK_5518Test.java

        Purpose:
                
        Description:
                
        History:
                Fri Apr 11 22:12:40 CST 2025, Created by jamson

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

@Tag("WcagTestOnly")
public class F102_ZK_5518Test extends WebDriverTestCase {

    @Test
    public void test() {
        connect();
        JQuery groupbox1 = jq("@groupbox:eq(0) .z-caption"),
            groupbox2 = jq("@groupbox:eq(1) .z-caption-content");
        Assertions.assertEquals("none", groupbox1.css("box-shadow"));
        getActions().sendKeys(Keys.TAB).perform();
        Assertions.assertNotEquals("none", groupbox1.css("box-shadow"));
        Assertions.assertEquals("none", groupbox2.css("box-shadow"));
        getActions().sendKeys(Keys.TAB, Keys.TAB).perform();
        Assertions.assertNotEquals("none", groupbox2.css("box-shadow"));
    }
}
