/* F102_ZK_5497Test.java

        Purpose:

        Description:

        History:
                Thu Apr 10 12:22:41 CST 2025, Created by jamson

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
public class F102_ZK_5497Test extends WebDriverTestCase {

    @Test
    public void test() {
        connect();
        JQuery tree = jq("@tree");
        Assertions.assertEquals("none", tree.css("box-shadow"));
        getActions().sendKeys(Keys.TAB).perform();
        Assertions.assertNotEquals("none", tree.css("box-shadow"));
    }
}
