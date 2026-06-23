/* B104_ZK_6051Test.java

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

/**
 * Desktop smoke test for ZK-6051 ("badge text in nav is too low").
 *
 * <p>The actual fix is an EE compact-tablet theme override
 * ({@code zkcml/zkmax/.../less/tablet/compact/_nav.less}: {@code top: auto}). A CE
 * zktest WebDriver test cannot exercise it at runtime: tablet UI is disabled by
 * default ({@code WEB-INF/zk.xml} {@code org.zkoss.zkmax.tablet.ui.disabled=true})
 * and the compact theme ({@code iceblue_c}) is not deployed in the zktest webapp,
 * so only the default desktop theme renders here. The badge-alignment runtime guard
 * belongs in zkcml test infra; see {@code zk-review-docs/ZK-6051-ignore-rules.md}
 * (IGN-6051-1).
 *
 * <p>This test therefore only smoke-checks, under the desktop theme, that the nav
 * and navitem badges render with their {@code badgeText} next to a label and without
 * JS errors — it deliberately does NOT assert pixel geometry (that would be a
 * false-green guard for the compact-tablet fix).
 */
public class B104_ZK_6051Test extends WebDriverTestCase {

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
