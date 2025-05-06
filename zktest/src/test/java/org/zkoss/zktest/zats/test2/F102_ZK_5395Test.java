/* F102_ZK_5395Test.java

        Purpose:

        Description:

        History:
                Thu Apr 10 11:02:54 CST 2025, Created by jamson

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
public class F102_ZK_5395Test extends WebDriverTestCase {

    @Test
    public void test() {
        connect();
        JQuery listbox = jq("@listbox");
        Assertions.assertEquals("none", listbox.css("box-shadow"));
        getActions().sendKeys(Keys.TAB).perform();
        Assertions.assertNotEquals("none", listbox.css("box-shadow"));
    }
}
