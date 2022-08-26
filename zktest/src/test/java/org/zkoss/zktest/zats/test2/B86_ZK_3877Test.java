/* B86_ZK_3877Test.java

        Purpose:
                
        Description:
                
        History:
                Fri Aug 24 17:48:15 CST 2018, Created by leon

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.interactions.Actions;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B86_ZK_3877Test extends WebDriverTestCase {
    @Test
    public void test() {
        Actions act = new Actions(connect());
        JQuery window = jq("@window");
        act.moveToElement(toElement(window), 199, 100).click().build().perform();
        assertTrue(window.hasClass("z-window-shadow"));
    }
}
