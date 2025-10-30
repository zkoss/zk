/* B102_ZK_5695Test.java

        Purpose:

        Description:

        History:
                Mon Apr 28 19:06:08 CST 2025, Created by cherrylee

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author cherrylee
 */
public class B102_ZK_5695Test extends WebDriverTestCase {

    @Test
    public void test() {
        connect();
        String visibleText = getEval("zk.Widget.$('$msg').$n().innerText.trim()");
        Assertions.assertEquals("in zscript", visibleText);

        String myVar = getEval("window.myVar");
        Assertions.assertEquals("in zscript", myVar);
    }
}


