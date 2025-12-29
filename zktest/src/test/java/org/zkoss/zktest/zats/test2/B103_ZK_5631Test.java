/* B103_ZK_5631Test.java

        Purpose:
                
        Description:
                
        History:
                Tue Dec 23 22:29:46 CST 2025, Created by peakerlee

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B103_ZK_5631Test extends WebDriverTestCase {
    @Test
    public void test() {
        connect();
        waitResponse();
        assertEquals("1000", jq("$lblVMCurpos").text());
        assertEquals("1000", jq("$lblVMMaxpos").text());
        assertEquals("1000.000",jq(".z-slider-button").attr("title"));
        assertNoAnyError();
    }
}
