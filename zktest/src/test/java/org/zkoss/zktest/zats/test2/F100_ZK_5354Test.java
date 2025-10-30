/* F100_ZK_5354Test.java

        Purpose:
                
        Description:
                
        History:
                Tue Sep 05 13:47:51 CST 2023, Created by rebeccalai

Copyright (C) 2023 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class F100_ZK_5354Test extends WebDriverTestCase {
    @Test
    public void test1() throws Exception {
        connect();
        waitResponse();
        String initCountBefore = jq("@iframe").contents().find("span").text();
        click(jq(".z-icon-angle-double-right"));
        waitResponse();
        click(jq(".z-icon-angle-double-left"));
        waitResponse();
        String initCountAfter = jq("@iframe").contents().find("span").text();
        System.out.println(initCountBefore + " vs " + initCountAfter);
        assertEquals(initCountBefore, initCountAfter);
    }

    @Test
    public void test2() throws Exception {
        connect("/test2/F100-ZK-5354-2.zul");
        waitResponse();
        click(jq("@tab:eq(1)"));
        waitResponse();
        click(jq("@tab:eq(2)"));
        waitResponse();
        assertNoJSError();
    }
}
