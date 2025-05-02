/* B102_ZK_5648Test.java

        Purpose:
                
        Description:
                
        History:
                Wed Apr 23 15:06:12 CST 2025, Created by jamson

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import java.util.Arrays;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B102_ZK_5648Test extends WebDriverTestCase {
    
    private static String TOP = "0px 2px 0px 0px",
            RIGHT = "-2px 0px 0px 0px",
            BOTTOM = "0px -2px 0px 0px",
            LEFT = "2px 0px 0px 0px";
    
    protected String CELL_CONTAINER = "@listitem",
        CELL = "@listcell";
    
    @Test
    public void test() {
        connect();
        getActions().sendKeys(Keys.TAB).perform();
        moveFocusDown();
        assertFocusOnNthItem(0);
        moveFocusDown();
        assertFocusOnNthItem(1);
        moveFocusDown();
        assertFocusOnNthItem(2);
    }
    
    private void moveFocusDown() {
        Keys ctrl = isMac() ? Keys.META : Keys.CONTROL;
        getActions().keyDown(ctrl).sendKeys(Keys.ARROW_DOWN).keyUp(ctrl).perform();
    }

    private void assertFocusOnNthItem(int index) {
        int m = jq(CELL_CONTAINER).length();
        Assertions.assertEquals(3, m);
        for (int i = 0; i < m; ++i) {
            JQuery $item = jq(CELL_CONTAINER).eq(i);
            int n = $item.find(CELL).length();
            Assertions.assertEquals(3, n);
            if (i == index) {
                for (int j = 0; j < n; ++j) {
                    JQuery $cell = $item.find(CELL).eq(j);
                    boolean leftExists = j == 0,
                            rightExists = j == n - 1;
                    assertBorderFocusExist($cell, new boolean[] {true, rightExists, true, leftExists});
                }
            } else {
                for (int j = 0; j < n; ++j) {
                    JQuery $cell = $item.find(CELL).eq(j);
                    assertBorderFocusExist($cell, new boolean[] {false, false, false, false});
                }
            }
        }
    }

    // borderExistsArray means whether {top, right, bottom, left} border focus style exists 
    private void assertBorderFocusExist(JQuery $cell, boolean[] borderExistsArray) {
        String[] parsed = Arrays.stream($cell.css("box-shadow")
                .replace("inset", "")
                .replaceAll("rgb\\([^)]*\\)", "")
                .split(","))
                .map(String::trim)
                .toArray(String[]::new);
        boolean[] actual = new boolean[borderExistsArray.length];
        for (String p : parsed) {
            if (TOP.equals(p)) actual[0] = true;
            else if (RIGHT.equals(p)) actual[1] = true;
            else if (BOTTOM.equals(p)) actual[2] = true;
            else if (LEFT.equals(p)) actual[3] = true;
        }
        Assertions.assertEquals(Arrays.toString(borderExistsArray), Arrays.toString(actual));
    }
}
