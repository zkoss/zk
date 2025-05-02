/* F102_ZK_5933Test.java

        Purpose:
                
        Description:
                
        History:
                Mon Apr 28 11:27:24 CST 2025, Created by jamson

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
public class F102_ZK_5933Test extends WebDriverTestCase {

    @Test
    public void test() {
        connect();
        JQuery cm = jq(".z-treecol-checkable");
        Assertions.assertEquals("none", cm.css("box-shadow"));
        getActions().sendKeys(Keys.TAB, Keys.ARROW_DOWN, Keys.ARROW_UP).perform();
        Assertions.assertNotEquals("none", cm.css("box-shadow"));
    }
}
