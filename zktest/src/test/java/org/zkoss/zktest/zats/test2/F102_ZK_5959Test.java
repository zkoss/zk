/* F102_ZK_5959Test.java

        Purpose:
                
        Description:
                
        History:
                Mon May 19 12:35:23 CST 2025, Created by jamson

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

@Tag("WcagTestOnly")
public class F102_ZK_5959Test extends WebDriverTestCase {

    @Test
    public void test() {
        connect();
        assertFocusedRowInner(-1, -1); // no focused cell
        getActions().sendKeys(Keys.TAB).perform();
        assertFocusedRowInner(0, 0);
        getActions().sendKeys(Keys.ARROW_RIGHT).perform();
        assertFocusedRowInner(0, 1);
        getActions().sendKeys(Keys.ARROW_DOWN).perform();
        assertFocusedRowInner(1, 1);
        getActions().sendKeys(Keys.ARROW_LEFT).perform();
        assertFocusedRowInner(1, 0);
    }

    private void assertFocusedRowInner(int y, int x) {
        JQuery rows = jq("@row");
        int m = rows.length();
        for (int i = 0; i < m; i++) {
            JQuery cells = rows.eq(i).find(".z-row-inner");
            int n = cells.length();
            for (int j = 0; j < n; j++) {
                JQuery cell = cells.eq(j);
                if (i == y && j == x) {
                    Assertions.assertNotEquals("none", cell.css("box-shadow"));
                } else {
                    Assertions.assertEquals("none", cell.css("box-shadow"));
                }
            }
        }
    }
}
