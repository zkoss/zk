/* B103_ZK_6019Test.java

        Purpose:
                
        Description:
                
        History:
                Thu Dec 04 16:40:17 CST 2025, Created by peakerlee

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B103_ZK_6019Test extends WebDriverTestCase {
    @Test
    public void test() {
        connect();
        int javascriptSemicolonCount = jq("a[href=\"javascript:;\"]").length();
        int javascriptVoidCount = jq("a[href=\"javascript:void(0);\"]").length();

        assertNoAnyError();
        Assertions.assertNotEquals(0, javascriptSemicolonCount);
        Assertions.assertEquals(0, javascriptVoidCount);
    }
}
