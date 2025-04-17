/* F102_ZK_5396Test.java

        Purpose:

        Description:

        History:
                Wed Apr 09 18:16:30 CST 2025, Created by jamson

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class F102_ZK_5396Test extends WebDriverTestCase {

    @Test
    public void test() {
        connect();
        JQuery cbDefault = jq("$cbdefault input"),
                cbTristate = jq("$cbtristate input");
        Assertions.assertEquals("none", cbDefault.css("box-shadow"));
        getActions().sendKeys(Keys.TAB).perform();
        Assertions.assertNotEquals("none", cbDefault.css("box-shadow"));
        Assertions.assertEquals("none", cbTristate.css("box-shadow"));
        getActions().sendKeys(Keys.TAB).perform();
        Assertions.assertNotEquals("none", cbTristate.css("box-shadow"));
    }
}
