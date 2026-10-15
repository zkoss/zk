/* B110_ZK_5584Test.java

        Purpose:
                
        Description:
                
        History:
                Fri Feb 06 12:27:40 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B110_ZK_5584Test extends WebDriverTestCase {
    @Test
    public void test() {
        connect();
        waitResponse();

        JQuery listboxBody = jq(".z-listbox-body");
        assertTrue(listboxBody.exists());

        int height = listboxBody.height();
        assertTrue(height > 0 && height <= 50);

        int scrollHeight = Integer.parseInt(getEval("jq('.z-listbox-body')[0].scrollHeight"));
        int clientHeight = Integer.parseInt(getEval("jq('.z-listbox-body')[0].clientHeight"));
        assertTrue(scrollHeight > clientHeight);
    }
}
