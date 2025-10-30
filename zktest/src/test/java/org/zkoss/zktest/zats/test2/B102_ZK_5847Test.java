/* B102_ZK_5847Test.java

        Purpose:
                
        Description:
                
        History:
                Tue May 20 16:09:46 CST 2025, Created by jamson

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import java.util.Collections;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeOptions;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B102_ZK_5847Test extends WebDriverTestCase {
    @Override
    protected ChromeOptions getWebDriverOptions() {
        return super.getWebDriverOptions()
                .setExperimentalOption("mobileEmulation", Collections.singletonMap("deviceName", "iPad"));
    }
    @Test
    public void test() {
        connect();
        int frozenCols = 3;
        JQuery columns = jq(".z-grid .z-columns .z-column"),
                rows = jq(".z-grid .z-grid-body .z-row");
        int[] colsOffsetLeft = new int[columns.length()];
        for (int i = 0; i < columns.length(); i++)
            colsOffsetLeft[i] = columns.eq(i).offsetLeft();

        jq(".z-frozen-inner").scrollLeft(100000);
        waitResponse();
        click(jq("@button"));
        waitResponse();

        for (int i = 0; i < columns.length(); i++) {
            int expected = colsOffsetLeft[i],
                    actual = columns.eq(i).offsetLeft();
            if (i < frozenCols)
                Assertions.assertEquals(expected, actual);
            else
                Assertions.assertNotEquals(expected, actual);
        }
        for (JQuery row : rows) {
            JQuery cells = row.find(".z-cell");
            for (int i = 0; i < cells.length(); i++) {
                int expected = colsOffsetLeft[i],
                        actual = cells.eq(i).offsetLeft();
                if (i < frozenCols)
                    Assertions.assertEquals(expected, actual);
                else
                    Assertions.assertNotEquals(expected, actual);
            }
        }
    }
}
