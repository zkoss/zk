/* B86_ZK_3735Test.java

        Purpose:
                
        Description:
                
        History:
                Wed Aug 22 11:45:52 CST 2018, Created by leon

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B86_ZK_3735Test extends WebDriverTestCase {
    @Test
    public void test() {
        connect();
        JQuery set1 = jq("$set1");
        JQuery set2 = jq("$set2");
        JQuery set3 = jq("$set3");
        click(set1);
        waitResponse();
        click(set2);
        waitResponse();
        click(set3);
        waitResponse();
        assertEquals("listboxSize : 1\n" +
                "visibleItemCount : 1\n" +
                "listboxSize : 1\n" +
                "visibleItemCount : 1\n" +
                "listboxSize : 8\n" +
                "visibleItemCount : 8",getZKLog());
    }
}
