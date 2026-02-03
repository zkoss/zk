/* B104_ZK_5523Test.java

        Purpose:
                
        Description:
                
        History:
                Wed Jan 21 16:09:23 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B104_ZK_5523Test extends WebDriverTestCase {
    @Test
    public void test() {
        connect();
        JQuery cb = jq("@chosenbox");

        click(cb);
        waitResponse();
        JQuery option0 = jq(".z-chosenbox-option").eq(0);
        String itemText = option0.text();
        click(option0);
        waitResponse();

        assertEquals(1, jq(".z-chosenbox-item").length());
        assertEquals(itemText, jq(".z-chosenbox-item-content").text());

        click(jq("@button:contains(invalidate)"));
        waitResponse();

        assertEquals(1, jq(".z-chosenbox-item").length());
        assertEquals(itemText, jq(".z-chosenbox-item-content").text());
    }
}
