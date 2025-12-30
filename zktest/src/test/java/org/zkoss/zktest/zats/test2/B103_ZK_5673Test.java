/* B103_ZK_5673Test.java

        Purpose:
                
        Description:
                
        History:
                Thu Dec 25 16:23:35 CST 2025, Created by peakerlee

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B103_ZK_5673Test extends WebDriverTestCase {
    @Test
    public void test() {
        connect();
        int initialWidth = jq("@textbox").eq(0).width();

        click(jq("$addBtn"));
        waitResponse();

        int finalWidth = jq("@textbox").eq(0).width();
        Assertions.assertEquals(initialWidth, finalWidth);
    }
}
