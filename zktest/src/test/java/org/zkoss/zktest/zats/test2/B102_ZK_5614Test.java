/* B102_ZK_5614Test.java

        Purpose:
                
        Description:
                
        History:
                Thu Apr 24 18:14:18 CST 2025, Created by jamson

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.startsWith;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B102_ZK_5614Test extends WebDriverTestCase {
    
    @Test
    public void test() {
        connect();
        JQuery x = jq(".z-datebox-input");
        focus(x);
        getActions().sendKeys("1967-05-01 00:00:00").perform();
        blur(x);
        waitResponse();
        // ZK-6167: the zone name is CLDR data (PST on JDK 11, MST on 17+, both at -8:00); the wall-clock time checks the offset
        assertThat(jq("$day").text(), allOf(startsWith("Mon May 01 00:00:00 "), endsWith(" 1967")));
    }
}
