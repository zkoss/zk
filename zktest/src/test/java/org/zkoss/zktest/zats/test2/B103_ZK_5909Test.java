/* B103_ZK_5909Test.java

        Purpose:

        Description:

        History:
                Wed Nov 19 12:37:42 CST 2025, Created by peggypeng

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B103_ZK_5909Test extends WebDriverTestCase {
    @Test
    public void test() {
        connect();
        JQuery nextPageBtn = jq(".z-paging-next");
        click(nextPageBtn);
        waitResponse();
        JQuery firstListItem = jq(".z-listitem").eq(0);
        click(firstListItem);
        JQuery setSelectionBtn = jq(".z-button");
        click(setSelectionBtn);
        waitResponse();
        Assertions.assertTrue(firstListItem.hasClass("z-listitem-selected"));
    }
}
