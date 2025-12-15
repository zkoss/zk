/* B103_ZK_5818Test.java

	Purpose:

	Description:

	History:
		Mon Nov 3 12:28:46 CST 2025, Created by peaker

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.interactions.Actions;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * Test for ZK-5818: Portal layout drag and drop functionality
 */
public class B103_ZK_5818Test extends WebDriverTestCase {
    @Test
    public void testPortalPanelDragAndDrop() {
        connect("/test2/B103-ZK-5818.zul");

        JQuery portalchildren = jq(".z-portalchildren");
        assertTrue(portalchildren.exists());

        JQuery panel1 = jq(".z-panel").eq(0);
        JQuery panel2 = jq(".z-panel").eq(1);
        JQuery panel3 = jq(".z-panel").eq(2);

        String panel1Id = panel1.attr("id");
        String panel2Id = panel2.attr("id");
        String panel3Id = panel3.attr("id");

        assertTrue(panel1.exists());
        assertTrue(panel2.exists());
        assertTrue(panel3.exists());

        Actions actions = new Actions(driver);

        actions.dragAndDrop(
            toElement(panel3.find(".z-panel-header")),
            toElement(panel1.find(".z-panel-header"))
        ).perform();
        waitResponse();

        panel1 = jq("#" + panel1Id);
        panel2 = jq("#" + panel2Id);

        actions.dragAndDrop(
            toElement(panel2.find(".z-panel-header")),
            toElement(panel1.find(".z-panel-header"))
        ).perform();
        waitResponse();

        assertTrue(jq("#" + panel1Id).exists());
        assertTrue(jq("#" + panel2Id).exists());
        assertTrue(jq("#" + panel3Id).exists());

        assertNoAnyError();
    }

    @Test
    public void testPortalPanelDragAndDrop2() {
        connect("/test2/B103-ZK-5818-2.zul");

        JQuery portalchildren = jq(".z-portalchildren");
        assertTrue(portalchildren.exists());

        JQuery panel1 = jq(".z-panel").eq(0);
        JQuery panel2 = jq(".z-panel").eq(1);
        JQuery panel3 = jq(".z-panel").eq(2);

        String panel1Id = panel1.attr("id");
        String panel2Id = panel2.attr("id");
        String panel3Id = panel3.attr("id");

        assertTrue(panel1.exists());
        assertTrue(panel3.exists());

        Actions actions = new Actions(driver);

        actions.dragAndDrop(
            toElement(panel3.find(".z-panel-header")),
            toElement(panel2.find(".z-panel-header"))
        ).perform();
        waitResponse();

        panel2 = jq("#" + panel2Id);
        panel3 = jq("#" + panel3Id);

        actions.dragAndDrop(
            toElement(panel2.find(".z-panel-header")),
            toElement(panel3.find(".z-panel-header"))
        ).perform();
        waitResponse();

        assertTrue(jq("#" + panel1Id).exists());
        assertTrue(jq("#" + panel2Id).exists());
        assertTrue(jq("#" + panel3Id).exists());

        assertNoAnyError();
    }
}
