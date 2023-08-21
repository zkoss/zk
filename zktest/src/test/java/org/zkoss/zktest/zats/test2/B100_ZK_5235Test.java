/* B100_ZK_5235Test.java

        Purpose:
                
        Description:
                
        History:
                Mon Aug 21 13:04:26 CST 2023, Created by rebeccalai

Copyright (C) 2023 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.interactions.Actions;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rebeccalai
 */
public class B100_ZK_5235Test extends WebDriverTestCase {
    @Test
    public void test() {
        Actions act = new Actions(connect());

        click(jq("$longMenu"));
        waitResponse();
        assertTrue(jq(".z-menupopup-open").isVisible());

        act.moveToElement(toElement(jq("$childMenu"))).perform();
        waitResponse();
        assertTrue(jq("$childMenu").hasClass("z-menu-hover"));
    }
}
