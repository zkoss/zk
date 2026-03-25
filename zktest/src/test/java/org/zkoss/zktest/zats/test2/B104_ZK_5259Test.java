/* B104_ZK_5259Test.java

        Purpose:
                
        Description:
                
        History:
                Fri Feb 06 15:13:07 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B104_ZK_5259Test extends WebDriverTestCase {
    @Test
    public void test() {
        connect();

        click(jq("@button"));
        waitResponse();

        JQuery radios = jq("@radio");
        assertEquals(2, radios.length());

        String logContent = jq("#zk_log").val();
        assertTrue(logContent.contains("test1") && logContent.contains("test2"));
    }
}
