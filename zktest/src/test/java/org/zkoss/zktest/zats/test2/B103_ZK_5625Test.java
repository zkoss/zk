/* B103_ZK_5625Test.java

        Purpose:
                
        Description:
                
        History:
                Fri Dec 26 15:59:22 CST 2025, Created by peakerlee

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;
public class B103_ZK_5625Test extends WebDriverTestCase {
    @Test
    public void test() {
        connect();
        waitResponse();

        verifyLayout("vlayout1", "5px");
        verifyLayout("vlayout2", "10px");
        verifyLayout("vlayout3", "auto");
        verifyLayout("hlayout1", "5px");
        verifyLayout("hlayout2", "20px");
        verifyLayout("hlayout3", "auto");

        assertNoAnyError();
    }

    private void verifyLayout(String id, String expected) {
        JQuery layout = jq("$" + id);
        JQuery inners = layout.children();
        int count = inners.length();

        boolean isVertical = layout.hasClass("z-vlayout");
        String paddingAttr = isVertical ? "padding-bottom" : "padding-right";

        boolean isAuto = "auto".equals(expected);
        String varValue = getEval("jq('$" + id + "')[0].style.getPropertyValue('--layout-spacing')").trim();

        if (isAuto) {
            Assertions.assertTrue(varValue.isEmpty());
        } else {
            Assertions.assertEquals(expected, varValue);
        }

        for (int i = 0; i < count; i++) {
            JQuery child = inners.eq(i);
            boolean isLast = (i == count - 1);

            String inlineStyle = child.attr("style");
            if (inlineStyle != null) {
                Assertions.assertFalse(inlineStyle.contains("padding"));
            }

            String actualPadding = child.css(paddingAttr);
            if (isAuto || isLast) {
                Assertions.assertTrue("0px".equals(actualPadding) || "0".equals(actualPadding));
            } else {
                Assertions.assertEquals(expected, actualPadding);
            }
        }
    }
}