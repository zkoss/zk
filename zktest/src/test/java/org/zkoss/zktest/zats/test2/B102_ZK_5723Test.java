/* B102_ZK_5723Test.java

        Purpose:
                
        Description:
                
        History:
                Wed Apr 30 16:57:35 CST 2025, Created by jamson

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B102_ZK_5723Test extends WebDriverTestCase {

    @Test
    public void test() {
        connect();
        JQuery tb = jq("$tb");
        String oriText = tb.val();
        getActions().moveToElement(toElement(tb))
                .clickAndHold()
                .moveByOffset(100, 0) 
                .release() 
                .sendKeys(Keys.BACK_SPACE)
                .perform();
        Assertions.assertNotEquals(oriText, tb.val());
        click(jq("@button"));
        Assertions.assertEquals("true", jq("$lb").text());
    }
}
