/* B102_ZK_5648Test.java

        Purpose:
                
        Description:
                
        History:
                Fri Apr 18 09:51:27 CST 2025, Created by cherrylee

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B102_ZK_5648Test extends WebDriverTestCase {

    @Test
    public void test() {
        connect();

        click(jq(".z-listheader-checkable"));
        waitResponse();

        click(jq(".z-listitem").eq(0));
        waitResponse();

        String bgA = jq(".z-listitem").eq(0).find(".z-listcell").css("background-color");
        String bgB = jq(".z-listitem").eq(1).find(".z-listcell").css("background-color");

        assertNotEquals(bgA, bgB);
    }
}




