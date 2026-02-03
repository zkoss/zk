/* B104_ZK_5259Test.java

    Purpose:
        
    Description:
        
    History:
        Mon Feb 02 16:50:53 CST 2026, Created by josephlo

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.zkoss.test.webdriver.WebDriverTestCase;

public class B104_ZK_5259Test extends WebDriverTestCase {
    @Test
    public void test() {
        connect();

        click(jq(".z-button"));
        waitResponse();
        String logText = jq("#zk_log").val();
        int radioCount = (int) Arrays.stream(logText.split("Radio", -1)).count() - 1;
        assertEquals(2, radioCount);
    }
}
