/* F102_ZK_5930Test.java

        Purpose:
                
        Description:
                
        History:
                Thu Apr 24 15:18:10 CST 2025, Created by jamson

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
public class F102_ZK_5930Test extends WebDriverTestCase {
    
    @Test
    public void test() {
        connect();
        assertFocusedCell(-1, -1); // no focused cell
        getActions().sendKeys(Keys.TAB).perform();
        assertFocusedCell(0, 0);
        getActions().sendKeys(Keys.ARROW_RIGHT).perform();
        assertFocusedCell(0, 1);
        getActions().sendKeys(Keys.ARROW_DOWN).perform();
        assertFocusedCell(1, 1);
        getActions().sendKeys(Keys.ARROW_LEFT).perform();
        assertFocusedCell(1, 0);
    }
    
    private void assertFocusedCell(int y, int x) {
        JQuery rows = jq("@row");
        int m = rows.length();
        for (int i = 0; i < m; i++) {
            JQuery cells = rows.eq(i).find("@cell");
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
