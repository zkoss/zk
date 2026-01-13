/* B103_ZK_5928Test.java

        Purpose:
                
        Description:
                
        History:
                Mon Jan 12 17:36:17 CST 2026, Created by josephlo

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.interactions.Actions;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B103_ZK_5928Test extends WebDriverTestCase {
    @Test
    public void test() {
        connect();
        JQuery splitter = jq(".z-south-splitter");
        JQuery arrowDown = jq(".z-tabbox-down-scroll");
        assertFalse(arrowDown.isVisible());
        Actions action = new Actions(driver);
        action.dragAndDropBy(toElement(splitter), 0, 300).build().perform();
        waitResponse();
        assertTrue(arrowDown.isVisible());
        action.dragAndDropBy(toElement(splitter), 0, -400).build().perform();
        waitResponse();
        assertFalse(arrowDown.isVisible());
    }
}