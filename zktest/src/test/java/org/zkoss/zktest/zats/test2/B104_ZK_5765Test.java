/* B104_ZK_5765Test.java

    Purpose:
        
    Description:
        
    History:
        Tue Jan 27 11:55:03 CST 2026, Created by josephlo

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;
import org.zkoss.test.webdriver.ztl.Widget;

public class B104_ZK_5765Test extends WebDriverTestCase {
    @Test
    public void test() {
        connect();

        Widget col1 = jq(".z-listheader:contains(col1)").toWidget();
        JQuery nextBtn = jq(".z-paging-next");

        col1.set("visible", false); 
        waitResponse();
        assertFalse(col1.is("visible"));

        click(nextBtn);
        waitResponse();

        col1.set("visible", true);
        waitResponse();
        
        assertTrue(col1.is("visible"));

        click(nextBtn);
        waitResponse();

        assertTrue(jq(col1).isVisible());
    }
}
