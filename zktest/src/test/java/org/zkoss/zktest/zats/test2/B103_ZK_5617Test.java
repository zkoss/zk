/* B103_ZK_5617Test.java

        Purpose:
                
        Description:
                
        History:
                Thu Dec 11 09:38:52 CST 2025, Created by peakerlee

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B103_ZK_5617Test  extends WebDriverTestCase {
    @Test
    public void test() {
        connect();
        click(jq(".z-toolbar-overflowpopup-button"));
        waitResponse();
        assertFalse(jq(".z-menu-popup .z-menu-item:contains(444)").exists());
        assertNoAnyError();
    }
}
