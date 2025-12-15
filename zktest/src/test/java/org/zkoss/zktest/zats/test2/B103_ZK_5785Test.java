/* B103_ZK_5785Test.java

        Purpose:
                
        Description:
                
        History:
                Thu Dec 04 22:17:24 CST 2025, Created by peakerlee

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B103_ZK_5785Test extends WebDriverTestCase {
    @Test
    public void test() {
        connect();
        int windowCountBefore = driver.getWindowHandles().size();
        click(jq("$AMailContacto"));
        waitResponse();
        assertNoAnyError();
        int windowCountAfter = driver.getWindowHandles().size();
        Assertions.assertEquals(windowCountBefore, windowCountAfter);
        assertNoAnyError();
    }
}