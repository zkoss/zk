/* B102_ZK_5922Test.java

        Purpose:
                
        Description:
                
        History:
                Tue Apr 15 14:35:10 CST 2025, Created by jamson

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B102_ZK_5922Test extends WebDriverTestCase {
    
    @Test
    public void test() {
        connect();
        
        JQuery input = jq(".z-doublespinner-input"),
                errorContent = jq(".z-errorbox-content");
        
        sendKeysToInput(input, "0");
        Assertions.assertTrue(errorContent.exists());
        Assertions.assertEquals("custom error", errorContent.text());

        sendKeysToInput(input, "1");
        Assertions.assertFalse(errorContent.exists());

        sendKeysToInput(input, "3");
        Assertions.assertTrue(errorContent.exists());
        Assertions.assertEquals("custom error", errorContent.text());
    }
    
    private void sendKeysToInput(JQuery input, String text) {
        focus(input);
        selectAll();
        getActions().sendKeys(Keys.BACK_SPACE, text).perform();
        blur(input);
        waitResponse();
    }
}
