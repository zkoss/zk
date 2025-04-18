/* B102_ZK_5818Test.java

        Purpose:
                
        Description:
                
        History:
                Thu Apr 17 17:01:28 CST 2025, Created by cherrylee

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.Element;
import org.zkoss.test.webdriver.ztl.JQuery;
import org.zkoss.test.webdriver.ztl.Widget;

/**
 * @author cherrylee
 */
public class B102_ZK_5818Test extends WebDriverTestCase {
    @Test
    public void test() {
        connect();
        waitResponse();

        JQuery jqPanels = jq("@panel");

        Widget panel1 = jqPanels.eq(0).toWidget();
        Widget panel2 = jqPanels.eq(1).toWidget();
        Widget panel3 = jqPanels.eq(2).toWidget();

        Element header1 = panel1.$n("head");
        Element header2 = panel2.$n("head");
        Element header3 = panel3.$n("head");

        getActions()
                .moveToElement(driver.findElement(header3))
                .clickAndHold()
                .moveToElement(driver.findElement(header1))
                .moveByOffset(0, -20)
                .release()
                .perform();
        waitResponse();

        panel1 = jq("@panel").eq(1).toWidget();
        panel2 = jq("@panel").eq(2).toWidget();

        header1 = panel1.$n("head");
        header2 = panel2.$n("head");

        getActions()
                .moveToElement(driver.findElement(header2))
                .clickAndHold()
                .moveToElement(driver.findElement(header1))
                .moveByOffset(0, -20)
                .release()
                .perform();
        waitResponse();

        assertEquals("3", jq("@panel").eq(0).toWidget().get("title"));
        assertEquals("2", jq("@panel").eq(1).toWidget().get("title"));
        assertEquals("1", jq("@panel").eq(2).toWidget().get("title"));
    }
}
