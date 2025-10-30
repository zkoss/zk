/* B102_ZK_5834Test.java

        Purpose:
                
        Description:
                
        History:
                Thu May 15 15:18:27 CST 2025, Created by jamson

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B102_ZK_5834Test extends WebDriverTestCase {
    @Test
    public void test() {
        connect();
        waitResponse();
        Assertions.assertEquals("500px", jq(".z-drawer-real").toElement().eval("style['width']"));
    }
}
