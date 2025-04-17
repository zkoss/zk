/* F102_ZK_5868Test.java

        Purpose:
                
        Description:
                
        History:
                Thu Apr 17 18:00:33 CST 2025, Created by jamson

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class F102_ZK_5868Test extends WebDriverTestCase {

    @Test
    public void test() {
        connect();
        JQuery first = jq(".z-paging-first"),
                previous = jq(".z-paging-previous"),
                next = jq(".z-paging-next"),
                last = jq(".z-paging-last");
        Assertions.assertEquals("none", first.css("box-shadow"));
        getActions().sendKeys(Keys.TAB).perform();
        Assertions.assertNotEquals("none", first.css("box-shadow"));
        Assertions.assertEquals("none", previous.css("box-shadow"));
        getActions().sendKeys(Keys.TAB).perform();
        Assertions.assertNotEquals("none", previous.css("box-shadow"));
        Assertions.assertEquals("none", next.css("box-shadow"));
        getActions().sendKeys(Keys.TAB, Keys.TAB).perform();
        Assertions.assertNotEquals("none", next.css("box-shadow"));
        Assertions.assertEquals("none", last.css("box-shadow"));
        getActions().sendKeys(Keys.TAB).perform();
        Assertions.assertNotEquals("none", last.css("box-shadow"));
    }
}
