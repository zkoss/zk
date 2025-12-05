/* B103_ZK_5903.java

        Purpose:

        Description:

        History:
                Fri Dec 05 09:39:33 CST 2025, Created by peggypeng

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B103_ZK_5903Test extends WebDriverTestCase {
    @Test
    public void test() {
        connect();

        JQuery input = jq(".z-decimalbox").eq(0);
        click(input);

        int listboxLeft = jq(".z-listbox").offsetLeft();
        int listboxRight = listboxLeft + jq(".z-listbox-body").outerWidth();
        int frozenColsWidth = jq(".z-listcell").eq(0).outerWidth() + jq(".z-listcell").eq(1).outerWidth();

        Actions actions = getActions();
        for (int i = 0; i < 7; i++) {
            actions.sendKeys(Keys.TAB).perform();
            waitResponse();

            JQuery focusedElement = jq(":focus");
            int focusedElementLeft = focusedElement.offsetLeft();

            boolean isInView = (focusedElementLeft >= listboxLeft + frozenColsWidth) && (focusedElementLeft < listboxRight);
            Assertions.assertTrue(isInView);
        }

        for (int i = 0; i < 7; i++) {
            actions.keyDown(Keys.SHIFT).sendKeys(Keys.TAB).keyUp(Keys.SHIFT).perform();
            waitResponse();

            JQuery focusedElement = jq(":focus");
            int focusedElementLeft = focusedElement.offsetLeft();

            boolean isInView = (focusedElementLeft >= listboxLeft + frozenColsWidth) && (focusedElementLeft < listboxRight);
            Assertions.assertTrue(isInView);
        }
    }
}
