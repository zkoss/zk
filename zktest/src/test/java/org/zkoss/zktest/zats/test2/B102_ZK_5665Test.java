/* B102_ZK_5665Test.java

        Purpose:
                
        Description:
                
        History:
                Thu Apr 24 18:17:21 CST 2025, Created by jamson

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.zkoss.test.webdriver.WebDriverTestCase;

public class B102_ZK_5665Test extends WebDriverTestCase {
    
    @Test
    public void test() {
        connect();
        sendKeys(jq("input[type=file]"), System.getProperty("user.dir") + "/src/main/webapp/test2/B101_ZK_5793.png");
        
        int count = 0;
        boolean buttonIsDisabled = false;
        while (count++ < 200) {
            sleep(10);
            if (jq("@button").attr("disabled").equals("disabled")) {
                buttonIsDisabled = true;
                break;
            }
        }

        assertTrue(buttonIsDisabled);
    }
}
