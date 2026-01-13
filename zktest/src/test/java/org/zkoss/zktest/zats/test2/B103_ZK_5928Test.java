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

        int tabsContentHeight = Integer.parseInt(getEval("zk.$('.z-tabs').$n('cave').scrollHeight"));
        int currentSouthHeight = jq(".z-south").outerHeight();

        int targetHeightSmall = tabsContentHeight / 2;
        int dragDownAmount = currentSouthHeight - targetHeightSmall;

        Actions action = new Actions(driver);
        action.dragAndDropBy(toElement(splitter), 0, dragDownAmount).perform();
        waitResponse();

        assertTrue(arrowDown.isVisible());

        int updatedSouthHeight = jq(".z-south").outerHeight();
        int targetHeightLarge = tabsContentHeight + 100;
        int dragUpAmount = -(targetHeightLarge - updatedSouthHeight);

        action.dragAndDropBy(toElement(splitter), 0, dragUpAmount).perform();
        waitResponse();

        assertFalse(arrowDown.isVisible());
    }
}