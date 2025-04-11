/* F102_ZK_5396Test.java

        Purpose:

        Description:

        History:
                Wed Apr 09 18:16:30 CST 2025, Created by jamson

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class F102_ZK_5396Test extends WebDriverTestCase {

    @Test
    public void test() {
        connect();
        JQuery[][] jqueries = new JQuery[][] {
                {
                    jq("$cbdefault input"),
                    jq("$cbtristate input"),
                    jq("$rd input"),
                    jq("$radiogrouprd1 input"),
                    jq("$radiogrouprd2 input"),
                },{
                    jq("$row1rd1 input"),
                    jq("$row1rd2 input"),
                    jq("$row2radiogrouprd1 input"),
                    jq("$row2radiogrouprd2 input"),
                    jq("$row3cb1 input"),
                    jq("$row3cb2 input")
                }
        };

        for (JQuery[] jqs : jqueries) {
            for (JQuery jq : jqs) {
                assertUiFocusExists(jq, false);
                if (jq.toString().contains("radiogrouprd2"))
                    right();
                else
                    tab();
                assertUiFocusExists(jq, true);
            }
            tab(); // for focus into grid
        }
    }

    private void tab() {
        getActions().sendKeys(Keys.TAB).perform();
    }

    private void right() {
        getActions().sendKeys(Keys.ARROW_RIGHT).perform();
    }

    private void assertUiFocusExists(JQuery e, boolean exists) {
        Assertions.assertEquals(exists, !"none".equals(e.css("box-shadow")));
    }
}
