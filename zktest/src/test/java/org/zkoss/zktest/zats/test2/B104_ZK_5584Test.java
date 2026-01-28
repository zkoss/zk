/* B104_ZK_5584Test.java

        Purpose:
                Test that listbox with CSS flex enabled shows scrollbar correctly

        Description:
                When using vflex="1" with CSS flex enabled, the listbox body 
                should have a height set so that overflow content can be scrolled.

        History:
                Tue Jan 28 14:28:00 CST 2026, Created by AI Assistant

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B104_ZK_5584Test extends WebDriverTestCase {
    @Test
    public void test() {
        connect();
        waitResponse();

        JQuery listboxBody = jq(".z-listbox-body");
        assertTrue(listboxBody.exists());

        String bodyHeight = listboxBody.css("height");
        assertTrue(bodyHeight != null && !bodyHeight.isEmpty() && !bodyHeight.equals("auto"));

        int height = Integer.parseInt(bodyHeight.replaceAll("[^0-9]", ""));
        assertTrue(height > 0 && height <= 60);

        int scrollHeight = Integer.parseInt(getEval("jq('.z-listbox-body')[0].scrollHeight"));
        int clientHeight = Integer.parseInt(getEval("jq('.z-listbox-body')[0].clientHeight"));
        assertTrue(scrollHeight > clientHeight);
    }
}
