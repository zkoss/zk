/* B103_ZK_5417.java

        Purpose:

        Description:

        History:
                Wed Nov 12 16:42:21 CST 2025, Created by peaker

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B103_ZK_5417Test extends WebDriverTestCase {
    @Test
    public void test() {
        connect();

        int initialScrollHeight = Integer.parseInt((String) getEval("document.documentElement.scrollHeight"));

        getEval("window.scrollTo(0, document.body.scrollHeight)");
        waitResponse();

        click(jq(".z-cascader"));
        waitResponse();

        click(jq(".z-cascader-item:contains(2)").first());
        waitResponse();

        click(jq(".z-cascader-item:contains(2.2)"));
        waitResponse();

        int finalScrollHeight = Integer.parseInt((String) getEval("document.documentElement.scrollHeight"));
        assertEquals(initialScrollHeight, finalScrollHeight);
        assertNoJSError();
    }
}
