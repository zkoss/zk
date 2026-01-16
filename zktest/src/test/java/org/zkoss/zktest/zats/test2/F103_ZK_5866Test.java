/* F103_ZK_5866Test.java

        Purpose:
                
        Description:
                
        History:
                Tue Dec 30 12:36:05 CST 2025, Created by peakerlee

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class F103_ZK_5866Test extends WebDriverTestCase {
    @Test
    public void testContains() {
        connect("/test2/F103-ZK-5866.zul");
        JQuery cb = jq("@chosenbox").eq(0);
        JQuery inp = cb.find(".z-chosenbox-input");

        click(cb);
        waitResponse();

        sendKeys(inp, "_JO");
        waitResponse();
        JQuery options = jq(".z-chosenbox-option");
        Assertions.assertEquals(1, getVisibleCount(options));
        type(inp, "");
        waitResponse();

        sendKeys(inp, "ka");
        waitResponse();
        options = jq(".z-chosenbox-option");
        Assertions.assertEquals(6, getVisibleCount(options));
        type(inp, "");
        waitResponse();

        sendKeys(inp, "_");
        waitResponse();
        options = jq(".z-chosenbox-option");
        Assertions.assertEquals(15, getVisibleCount(options));
        type(inp, "");
        waitResponse();

        sendKeys(inp, "a");
        waitResponse();
        options = jq(".z-chosenbox-option");
        Assertions.assertEquals(15, getVisibleCount(options));
        assertNoJSError();
    }

    @Test
    public void testStartsWith() {
        connect("/test2/F103-ZK-5866Default.zul");

        JQuery cb = jq("@chosenbox").eq(0);
        JQuery inp = cb.find(".z-chosenbox-input");
        String ppId = cb.attr("id") + "-pp";

        click(cb);
        waitResponse();

        sendKeys(inp, "_JO");
        waitResponse();
        JQuery options = jq(".z-chosenbox-option");
        Assertions.assertEquals(0, getVisibleCount(options));
        type(inp, "");
        waitResponse();

        sendKeys(inp, "ka");
        waitResponse();
        options = jq(".z-chosenbox-option");
        Assertions.assertEquals(6, getVisibleCount(options));
        type(inp, "");
        waitResponse();

        sendKeys(inp, "_");
        waitResponse();
        options = jq(".z-chosenbox-option");
        Assertions.assertEquals(0, getVisibleCount(options));
        type(inp, "");
        waitResponse();

        sendKeys(inp, "a");
        waitResponse();
        options = jq(".z-chosenbox-option");
        Assertions.assertEquals(15, getVisibleCount(options));

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