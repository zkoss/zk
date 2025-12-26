/* B103_ZK_5662Test.java

        Purpose:
                
        Description:
                
        History:
                Tue Dec 16 17:16:29 CST 2025, Created by peakerlee

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;


import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B103_ZK_5662Test extends WebDriverTestCase {
    @Test
    public void test() {
        connect();
        waitResponse();
        assertNoAnyError();
    }
}
