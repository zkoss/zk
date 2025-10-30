/* B102_ZK_5914Test.java

        Purpose:

        Description:

        History:
                Sat Mar 29 14:51:19 CST 2025, Created by jamson

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B102_ZK_5914Test extends WebDriverTestCase {

    @Test
    public void test() {
        connect();

        JQuery blockGroups = jq("$blockLevelDisplayModes > .group");
        for (int i = 0; i < blockGroups.length(); i++) {
            JQuery children = blockGroups.eq(i).find(".container > @div");
            Assertions.assertEquals(3, children.length());
            for (int j = 0; j < children.length(); ++j)
                Assertions.assertEquals(10 + (j * 10), children.eq(j).height());
        }

        JQuery inlineGroups = jq("$inlineLevelDisplayModes > .group");
        for (int i = 0; i < inlineGroups.length(); i++) {
            JQuery children = inlineGroups.eq(i).find(".container > @div");
            Assertions.assertEquals(3, children.length());
            for (int j = 0; j < children.length(); ++j)
                Assertions.assertEquals(0, children.eq(j).height() * children.eq(j).width());
        }
    }
}