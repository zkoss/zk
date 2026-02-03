/* B104_ZK_5689Test.java

        Purpose:
                
        Description:
                
        History:
                Thu Jan 22 11:54:57 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.zkoss.test.webdriver.WebDriverTestCase;

public class B104_ZK_5689Test extends WebDriverTestCase {
    @Test
    public void test() {
        connect();
        String zkFontFamily = jq("$btn-zk i").css("font-family");
        zkFontFamily = zkFontFamily.replace("\"", "").replace("'", "");
        assertTrue(zkFontFamily.contains("FontAwesome"));
        String extFontFamily = jq("$btn-fas i").css("font-family");
        extFontFamily = extFontFamily.replace("\"", "").replace("'", "");
        assertNotEquals("FontAwesome", extFontFamily);
    }
}
