/* B103_ZK_6047Test.java

        Purpose:
                
        Description:
                
        History:
                Fri Jan 16 14:17:35 CST 2026, Created by josephlo

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B103_ZK_6047Test extends WebDriverTestCase {
    @Test
    public void test() {
        connect();

        JQuery btn = jq("@button");
        click(btn);
        waitResponse();
        assertTrue(jq(".z-notification").exists());
        sleep(3000);
        waitResponse();
        getEval("window.scrollBy(0,50)");
        waitResponse();

        click(btn);
        waitResponse();
        assertTrue(jq(".z-notification").exists());
    }
}