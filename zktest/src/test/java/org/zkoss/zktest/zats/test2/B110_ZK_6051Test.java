/* B110_ZK_6051Test.java

        Purpose:

        Description:

        History:
                Fri Feb 06 18:10:38 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B110_ZK_6051Test extends WebDriverTestCase {

    @Test
    public void test() {
        connect();
        waitResponse();
        assertNoJSError();

        // <nav badgeText="3">: vertical (eq 0) and horizontal (eq 1) navbar
        assertBadgeRendered(jq(".z-nav-info").eq(0), ".z-nav-text", "3", "vertical nav");
        assertBadgeRendered(jq(".z-nav-info").eq(1), ".z-nav-text", "3", "horizontal nav");

        // <navitem badgeText="5">: vertical (eq 0) and horizontal (eq 1) navbar
        assertBadgeRendered(jq(".z-navitem-info").eq(0), ".z-navitem-text", "5", "vertical navitem");
        assertBadgeRendered(jq(".z-navitem-info").eq(1), ".z-navitem-text", "5", "horizontal navitem");
    }

    private void assertBadgeRendered(JQuery badge, String labelSelector, String expectedText, String which) {
        assertTrue(badge.exists(), which + ": badge not rendered");
        assertEquals(expectedText, badge.text(), which + ": badge text");
        assertTrue(badge.parent().find(labelSelector).exists(), which + ": label not rendered");
    }
}
