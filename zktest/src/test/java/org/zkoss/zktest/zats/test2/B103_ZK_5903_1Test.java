/* B103_ZK_5903_1Test.java

        Purpose:

        Description:

        History:
                Fri Dec 05 11:41:54 CST 2025, Created by peggypeng

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B103_ZK_5903_1Test extends WebDriverTestCase {
    @Test
    public void test() {
        connect();

        int listboxLeft = jq(".z-listbox").offsetLeft();
        int listboxRight = listboxLeft + jq(".z-listbox-body").outerWidth();

        JQuery inputInCase1 = jq(".z-decimalbox").eq(0);
        click(inputInCase1);
        waitResponse();

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

        for (int i = 0; i < 3; i++) {
            actions.keyDown(Keys.SHIFT).sendKeys(Keys.TAB).keyUp(Keys.SHIFT).perform();
            waitResponse();

            JQuery focusedElement = jq(":focus");
            int focusedElementLeft = focusedElement.offsetLeft();

            boolean isInView = (focusedElementLeft >= listboxLeft + frozenColsWidth) && (focusedElementLeft < listboxRight);
            Assertions.assertTrue(isInView);
        }

        JQuery inputInCase2 = jq(".z-decimalbox").eq(12);
        click(inputInCase2);
        waitResponse();

        for (int i = 0; i < 7; i++) {
            actions.sendKeys(Keys.TAB).perform();
            waitResponse();

            JQuery focusedElement = jq(":focus");
            int focusedElementLeft = focusedElement.offsetLeft();

            boolean isInView = (focusedElementLeft >= listboxLeft) && (focusedElementLeft < listboxRight);
            Assertions.assertTrue(isInView);
        }

        actions.keyDown(Keys.SHIFT).sendKeys(Keys.TAB).keyUp(Keys.SHIFT).perform();
        waitResponse();

        JQuery focusedElement = jq(":focus");
        int focusedElementLeft = focusedElement.offsetLeft();

        boolean isInView = (focusedElementLeft >= listboxLeft) && (focusedElementLeft < listboxRight);
        Assertions.assertTrue(isInView);
    }
}
