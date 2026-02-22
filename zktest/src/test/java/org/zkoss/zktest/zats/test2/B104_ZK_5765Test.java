/* B104_ZK_5765Test.java

    Purpose:
        
    Description:
        
    History:
		Thu Feb 05 18:27:00 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.zkoss.test.webdriver.WebDriverTestCase;

public class B104_ZK_5765Test extends WebDriverTestCase {
    @Override
    protected boolean isHeadless() {
        // TODO Auto-generated method stub
        return false;
    }
    
    @Test
    public void test() {
        connect();

        assertTrue(jq("@listheader:eq(0)").isVisible());

        mouseOver(jq("@listheader:eq(0)"));
        waitResponse();
        click(jq("@listheader:eq(0) .z-listheader-button"));
        waitResponse();
        click(jq("@menupopup @menuitem:contains(col1)"));
        waitResponse();

        click(jq(".z-paging-next"));
        waitResponse();

        mouseOver(jq("@listheader:eq(1)"));
        waitResponse();
        click(jq("@listheader:eq(1) .z-listheader-button"));
        waitResponse();
        click(jq("@menupopup @menuitem:contains(col1)"));
        waitResponse();

        assertTrue(jq("@listheader:eq(0)").isVisible());

        click(jq(".z-paging-next"));
        waitResponse();

        assertTrue(jq("@listheader:eq(0)").isVisible());
    }
}
