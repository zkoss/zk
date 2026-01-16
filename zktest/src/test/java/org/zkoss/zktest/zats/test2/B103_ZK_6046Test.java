/* B103_ZK_6046Test.java

        Purpose:
                
        Description:
                
        History:
                Wed Jan 14 17:16:41 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B103_ZK_6046Test extends WebDriverTestCase {
    @Test
    public void test() {
        connect();

        JQuery upBtn = jq(".z-spinner-up");
        JQuery downBtn = jq(".z-spinner-down");

        String upBtnColor = upBtn.css("color");
        String downBtnColor = downBtn.css("color");
        String upBtnBgColor = upBtn.css("background-color");
        String downBtnBgColor = downBtn.css("background-color");

        getActions().moveToElement(toElement(upBtn)).clickAndHold().pause(500).perform();

        boolean upChanged = !upBtn.css("color").equals(upBtnColor) || !upBtn.css("background-color").equals(upBtnBgColor);
        Assertions.assertTrue(upChanged);

        Assertions.assertEquals(downBtnColor, downBtn.css("color"));
        Assertions.assertEquals(downBtnBgColor, downBtn.css("background-color"));

        getActions().release().perform();
        getActions().moveToElement(toElement(jq("body"))).perform();
        waitResponse();

        Assertions.assertEquals(upBtnColor, upBtn.css("color"));
        Assertions.assertEquals(upBtnBgColor, upBtn.css("background-color"));

        getActions().moveToElement(toElement(downBtn)).clickAndHold().pause(500).perform();

        boolean downChanged = !downBtn.css("color").equals(downBtnColor) || !downBtn.css("background-color").equals(downBtnBgColor);
        Assertions.assertTrue(downChanged);

        Assertions.assertEquals(upBtnColor, upBtn.css("color"));
        Assertions.assertEquals(upBtnBgColor, upBtn.css("background-color"));

        getActions().release().perform();
    }
}
