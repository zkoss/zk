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
        JQuery arrowUp = jq(".z-tabbox-up-scroll");

        String tabsContentHeightStr = getEval("zk.$('.z-tabs').$n('cave').scrollHeight");
        int tabsContentHeight;
        try {
            tabsContentHeight = Integer.parseInt(tabsContentHeightStr);
        } catch (NumberFormatException e) {
            throw new AssertionError("Unexpected non-numeric tabs content height: " + tabsContentHeightStr, e);
        }
        int currentSouthHeight = jq(".z-south").outerHeight();
        int tabsVisibleHeight = jq(".z-tabs").outerHeight();
        int overhead = currentSouthHeight - tabsVisibleHeight;
        Actions action = getActions();

        int targetSouthHeight = tabsContentHeight / 2 + overhead;
        int drag1Distance = currentSouthHeight - targetSouthHeight;
        action.clickAndHold(toElement(splitter))
            .moveByOffset(0, drag1Distance)
            .release()
            .build()
            .perform();
        waitResponse();
        sleep(300);
        assertTrue(arrowDown.isVisible());
        assertTrue(arrowUp.isVisible());

        int southHeight = jq(".z-south").outerHeight();
        int noOverflowSouthHeight = tabsContentHeight + overhead + 60;
        int drag2Distance = southHeight - noOverflowSouthHeight;
        action.clickAndHold(toElement(splitter))
            .moveByOffset(0, drag2Distance)
            .release()
            .build()
            .perform();
        waitResponse();
        sleep(300);

        // Fallback: if arrows still visible due to rounding, expand further
        if (arrowDown.isVisible() || arrowUp.isVisible()) {
            action.clickAndHold(toElement(splitter))
                .moveByOffset(0, -150)
                .release()
                .build()
                .perform();
            waitResponse();
            sleep(300);
        }

        // Second fallback if still visible
        if (arrowDown.isVisible() || arrowUp.isVisible()) {
            action.clickAndHold(toElement(splitter))
                .moveByOffset(0, -150)
                .release()
                .build()
                .perform();
            waitResponse();
            sleep(300);
        }

        assertFalse(arrowDown.isVisible());
        assertFalse(arrowUp.isVisible());
    }
}