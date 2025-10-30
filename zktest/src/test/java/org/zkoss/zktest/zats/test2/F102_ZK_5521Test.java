/* F102_ZK_5521Test.java

        Purpose:
                
        Description:
                
        History:
                Thu Apr 10 16:33:27 CST 2025, Created by jamson

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
public class F102_ZK_5521Test extends WebDriverTestCase {

    @Test
    public void test() {
        connect();
        JQuery navitem = jq(".z-navitem-content"),
                nav = jq(".z-nav-content"),
                navitemInner1 = jq(".z-navitem-content:eq(1)"),
                navitemInner2 = jq(".z-navitem-content:eq(2)");
        
        Assertions.assertEquals("none", navitem.css("box-shadow"));
        getActions().sendKeys(Keys.TAB).perform();
        Assertions.assertNotEquals("none", navitem.css("box-shadow"));
        Assertions.assertEquals("none", nav.css("box-shadow"));
        getActions().sendKeys(Keys.ARROW_DOWN).perform();
        Assertions.assertNotEquals("none", nav.css("box-shadow"));
        Assertions.assertEquals("none", navitemInner1.css("box-shadow"));
        getActions().sendKeys(Keys.ARROW_RIGHT).perform();
        Assertions.assertNotEquals("none", navitemInner1.css("box-shadow"));
        Assertions.assertEquals("none", navitemInner2.css("box-shadow"));
        getActions().sendKeys(Keys.ARROW_DOWN).perform();
        Assertions.assertEquals("none", navitemInner1.css("box-shadow"));
        Assertions.assertNotEquals("none", navitemInner2.css("box-shadow"));

        navitem = jq(".z-navitem-content:eq(3)");
        nav = jq(".z-nav-content:eq(1)");
        navitemInner1 = jq(".z-navitem-content:eq(4)");
        navitemInner2 = jq(".z-navitem-content:eq(5)");

        Assertions.assertEquals("none", navitem.css("box-shadow"));
        getActions().sendKeys(Keys.TAB).perform();
        Assertions.assertNotEquals("none", navitem.css("box-shadow"));
        Assertions.assertEquals("none", nav.css("box-shadow"));
        getActions().sendKeys(Keys.ARROW_RIGHT).perform();
        Assertions.assertNotEquals("none", nav.css("box-shadow"));
        Assertions.assertEquals("none", navitemInner1.css("box-shadow"));
        getActions().sendKeys(Keys.ARROW_DOWN).perform();
        Assertions.assertNotEquals("none", navitemInner1.css("box-shadow"));
        Assertions.assertEquals("none", navitemInner2.css("box-shadow"));
        getActions().sendKeys(Keys.ARROW_RIGHT).perform();
        Assertions.assertEquals("none", navitemInner1.css("box-shadow"));
        Assertions.assertNotEquals("none", navitemInner2.css("box-shadow"));
    }
}
