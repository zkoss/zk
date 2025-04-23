/* F102_ZK_5474Test.java

        Purpose:
                
        Description:
                
        History:
                Tue Apr 15 17:06:50 CST 2025, Created by jamson

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class F102_ZK_5474Test extends WebDriverTestCase {

    @Test
    public void test() {
        connect();

        JQuery[] numberInputs = new JQuery[]{
                jq("@decimalbox"),
                jq("@intbox"),
                jq("@longbox"),
                jq("@doublebox")
        };
        JQuery errorContent = jq(".z-errorbox-content");
        
        for (JQuery numberInput : numberInputs) {
            sendKeysToInput(numberInput, "0");
            Assertions.assertTrue(errorContent.exists());
            Assertions.assertEquals("custom error", errorContent.text());

            sendKeysToInput(numberInput, "1");
            Assertions.assertFalse(errorContent.exists());

            sendKeysToInput(numberInput, "3");
            Assertions.assertTrue(errorContent.exists());
            Assertions.assertEquals("custom error", errorContent.text());

            sendKeysToInput(numberInput, "2");
            Assertions.assertFalse(errorContent.exists());
        }
    }

    private void sendKeysToInput(JQuery input, String text) {
        focus(input);
        selectAll();
        getActions().sendKeys(Keys.BACK_SPACE, text).perform();
        blur(input);
        waitResponse();
    }
}
