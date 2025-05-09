/* B102_ZK_5805Test.java

        Purpose:
                
        Description:
                
        History:
                Tue May 06 15:05:14 CST 2025, Created by jamson

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B102_ZK_5805Test extends WebDriverTestCase {

    @Test
    public void test() {
        connect();

        click(jq("@menu"));
        waitResponse();

        getActions().sendKeys(Keys.ARROW_DOWN).perform();
        assertFocusOnNthMenuitem(0);
        getActions().sendKeys(Keys.ENTER).perform();
        waitResponse();
        Assertions.assertEquals("A", jq("#zk_log").val().replace("\n", ""));

        getActions().sendKeys(Keys.ARROW_DOWN).perform();
        assertFocusOnNthMenuitem(1);
        getActions().sendKeys(Keys.ESCAPE).perform();
        waitResponse();
        Assertions.assertEquals("AB", jq("#zk_log").val().replace("\n", ""));

        getActions().sendKeys(Keys.ARROW_DOWN).perform();
        assertFocusOnNthMenuitem(2);
        getActions().sendKeys(Keys.BACK_SPACE).perform();
        waitResponse();
        Assertions.assertEquals("ABC", jq("#zk_log").val().replace("\n", ""));

        getActions().sendKeys(Keys.ARROW_DOWN).perform();
        assertFocusOnNthMenuitem(0);
    }

    private void assertFocusOnNthMenuitem(int focusItemIndex) {
        JQuery mis = jq("@menuitem");
        for (int i = 0; i < mis.length(); ++i) {
            JQuery mi = mis.eq(i);
            if (i == focusItemIndex) {
                Assertions.assertTrue(mi.hasClass("z-menuitem-focus"));
            } else {
                Assertions.assertFalse(mi.hasClass("z-menuitem-focus"));
            }
        }
    }
}
