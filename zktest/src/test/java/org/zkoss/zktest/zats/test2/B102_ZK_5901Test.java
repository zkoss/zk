/* B102_ZK_5901Test.java

        Purpose:
                
        Description:
                
        History:
                Wed May 21 22:19:18 CST 2025, Created by jamson

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B102_ZK_5901Test extends WebDriverTestCase {
    @Test
    public void test() {
        connect();
        JQuery toolbarbutton = jq("@toolbarbutton");
        String unfocusedBorderColor = toolbarbutton.css("border-color");
        getActions().sendKeys(Keys.TAB).perform();
        Assertions.assertNotEquals(unfocusedBorderColor, toolbarbutton.css("border-color"));
        String uncheckedBackgroundColor = toolbarbutton.css("background-color");
        click(toolbarbutton);
        Assertions.assertNotEquals(uncheckedBackgroundColor, toolbarbutton.css("background-color"));
    }
}
