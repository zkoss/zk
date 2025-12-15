/* B103_ZK_5690Test.java

        Purpose:
                
        Description:
                
        History:
                Wed Dec 10 23:25:43 CST 2025, Created by peakerlee

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.interactions.Actions;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B103_ZK_5690Test  extends WebDriverTestCase {
    @Test
    public void test() {
        connect();
        waitResponse();

        Actions actions = new Actions(driver);
        actions.moveToElement(toElement(jq(".z-menu"))).perform();
        waitResponse();
        Assertions.assertTrue(jq(".z-menupopup").isVisible());

        actions.moveToElement(toElement(jq("body"))).perform();
        sleep(1000);
        waitResponse();
        Assertions.assertFalse(jq(".z-menupopup").isVisible());
        assertNoAnyError();
    }
}
