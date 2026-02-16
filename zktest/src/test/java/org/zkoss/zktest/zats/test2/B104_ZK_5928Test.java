/* B104_ZK_5928Test.java

        Purpose:
                
        Description:
                
        History:
                Mon Feb 09 17:35:11 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.interactions.Actions;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B104_ZK_5928Test extends WebDriverTestCase {
    @Test
    public void test() {
        connect();
        JQuery splitter = jq(".z-south-splitter");
        JQuery arrowDown = jq(".z-tabbox-down-scroll");

        String tabsContentHeightStr = getEval("zk.$('.z-tabs').$n('cave').scrollHeight");
        int tabsContentHeight;
        try {
            tabsContentHeight = Integer.parseInt(tabsContentHeightStr);
        } catch (NumberFormatException e) {
            throw new AssertionError("Unexpected non-numeric tabs content height: " + tabsContentHeightStr, e);
        }
        int currentSouthHeight = jq(".z-south").outerHeight();

        int targetHeightSmall = tabsContentHeight / 2;
        int dragDownAmount = currentSouthHeight - targetHeightSmall;

        Actions action = getActions();
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