/* B103_ZK_5254Test.java

        Purpose:

        Description:

        History:
                Fri Nov 07 10:11:54 CST 2025, Created by peggypeng

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B103_ZK_5254Test extends WebDriverTestCase {
    @Test
    public void test() {
        connect();

        JQuery originalFirstTreeItem = jq(".z-treerow").eq(0);
        String originalFirstItemText = originalFirstTreeItem.find(".z-treecell-text").text();
        click(originalFirstTreeItem);

        Actions actions = getActions();
        for (int i = 0; i < 10; i++) {
            actions.sendKeys(Keys.ARROW_DOWN);
        }
        actions.perform();
        waitResponse();

        JQuery clearTreeSelectionBtn = jq("@button");
        click(clearTreeSelectionBtn);
        waitResponse();

        String scrollTopStr = getEval("jq('.z-tree-body')[0].scrollTop"); // the scroll position
        String rowHeightStr = getEval("jq('.z-treerow').first().outerHeight()");

        int scrollTop = (int) Double.parseDouble(scrollTopStr);
        int rowHeight = (int) Double.parseDouble(rowHeightStr);
        int firstVisibleIndex = scrollTop / rowHeight;

        JQuery currentFirstVisibleItem = jq(".z-treerow").eq(firstVisibleIndex);
        String currentFirstItemText = currentFirstVisibleItem.find(".z-treecell-text").text();

        // after setting the selection to null, the tree should not scroll back to the top
        Assertions.assertNotEquals(originalFirstItemText, currentFirstItemText);
    }
}
