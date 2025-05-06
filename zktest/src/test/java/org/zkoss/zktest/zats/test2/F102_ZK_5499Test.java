/* F102_ZK_5499Test.java

        Purpose:
                
        Description:
                
        History:
                Thu Apr 10 12:59:34 CST 2025, Created by jamson

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
public class F102_ZK_5499Test extends WebDriverTestCase {

    @Test
    public void test() {
        connect();
        JQuery treecol = jq("@treecol");
        Assertions.assertEquals("none", treecol.css("box-shadow"));
        getActions().sendKeys(Keys.TAB, Keys.ARROW_DOWN, Keys.ARROW_UP).perform();
        Assertions.assertNotEquals("none", treecol.css("box-shadow"));
    }
}
