/* B103_ZK_5865Test.java

        Purpose:

        Description:

        History:
                Mon Dec 29 15:44:18 CST 2025, Created by peakerlee

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class F103_ZK_5865Test extends WebDriverTestCase{
    @Test
    public void test() {
        connect("/test2/F103-ZK-5865.zul");
        JQuery cb1 = jq("$chosenbox1");
        JQuery cb1Inp = cb1.find(".z-chosenbox-input");
        click(cb1);
        waitResponse();
        JQuery options1 = jq(".z-chosenbox-select").find(".z-chosenbox-option");
        long visibleCount = getVisibleCount(options1);
        Assertions.assertEquals(5, visibleCount);
        sendKeys(cb1Inp, "a");
        waitResponse();
        JQuery currentOptions1 = jq(".z-chosenbox-select").find(".z-chosenbox-option");
        Assertions.assertEquals(1, getVisibleCount(currentOptions1));
        assertNoAnyError();
    }


    @Test
    public void test2() {
        connect("/test2/F103-ZK-5865-2.zul");
        JQuery cb2 = jq("$chosenbox2");
        JQuery cb2Inp = cb2.find(".z-chosenbox-input");
        click(cb2);
        waitResponse();
        JQuery options2 = jq(".z-chosenbox-select").find(".z-chosenbox-option");
        long visibleCount = getVisibleCount(options2);
        Assertions.assertEquals(15, visibleCount);
        sendKeys(cb2Inp, "kk");
        waitResponse();
        JQuery currentOptions2 = jq(".z-chosenbox-select").find(".z-chosenbox-option");
        Assertions.assertEquals(4, getVisibleCount(currentOptions2));
        assertNoAnyError();
    }


    @Test
    public void test3() {
        connect("/test2/F103-ZK-5865-3.zul");
        JQuery cb3 = jq("$chosenbox3");
        JQuery cb3Inp = cb3.find(".z-chosenbox-input");
        click(cb3);
        waitResponse();
        JQuery options3 = jq(".z-chosenbox-select").find(".z-chosenbox-option");
        long visibleCount = getVisibleCount(options3);
        Assertions.assertEquals(10, visibleCount);
        sendKeys(cb3Inp, "ar_E");
        waitResponse();
        JQuery currentOptions3 = jq(".z-chosenbox-select").find(".z-chosenbox-option");
        Assertions.assertEquals(3, getVisibleCount(currentOptions3));
        assertNoJSError();
    }


    @Test
    public void test4() {
        connect("/test2/F103-ZK-5865-4.zul");
        JQuery cb4 = jq("$chosenbox4");
        JQuery cb4Inp = cb4.find(".z-chosenbox-input");
        click(cb4);
        waitResponse();
        JQuery options4 = jq(".z-chosenbox-select").find(".z-chosenbox-option");
        long visibleCount = getVisibleCount(options4);
        Assertions.assertEquals(100, visibleCount);
        sendKeys(cb4Inp, "ar_E");
        waitResponse();
        JQuery currentOptions4 = jq(".z-chosenbox-select").find(".z-chosenbox-option");
        Assertions.assertEquals(3, getVisibleCount(currentOptions4));
        type(cb4Inp, "");
        waitResponse();
        currentOptions4 = jq(".z-chosenbox-select").find(".z-chosenbox-option");
        Assertions.assertEquals(100, getVisibleCount(currentOptions4));
        assertNoJSError();
    }

    private long getVisibleCount(JQuery items) {
        int count = 0;
        for (int i = 0; i < items.length(); i++) {
            if (!"none".equals(items.eq(i).css("display")) && !"hidden".equals(items.eq(i).css("visibility"))) {
                count++;
            }
        }
        return count;
    }
}