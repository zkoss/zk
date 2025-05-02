/* F102_ZK_5926Test.java

        Purpose:
                
        Description:
                
        History:
                Thu Apr 17 16:14:53 CST 2025, Created by jamson

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
public class F102_ZK_5926Test extends WebDriverTestCase {

    @Test
    public void test() {
        connect();
        JQuery column = jq("@column");
        Assertions.assertEquals("none", column.css("box-shadow"));
        getActions().sendKeys(Keys.TAB, Keys.ARROW_UP).perform();
        Assertions.assertNotEquals("none", column.css("box-shadow"));
    }
}
