/* F104_ZK_6059Test.java

        Purpose:
                
        Description:
                
        History:
                Fri Feb 06 16:39:14 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * Test for ZK-6050: HTML element should have lang attribute for WCAG 2.2
 * compliance.
 * This ensures screen readers can properly announce page content.
 */
public class F104_ZK_6059Test extends WebDriverTestCase {
    @Test
    public void testHtmlLangAttribute() {
        connect();

        JQuery html = jq("html");
        String lang = html.attr("lang");

        assertNotNull(lang, "HTML element should have a lang attribute");
        assertFalse(lang.isEmpty(), "HTML lang attribute should not be empty");
    }
}
