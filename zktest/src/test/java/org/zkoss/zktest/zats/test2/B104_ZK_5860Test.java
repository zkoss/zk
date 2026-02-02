/* B104_ZK_5860Test.java

    Purpose:
        
    Description:
        
    History:
        Thu Jan 29 18:17:25 CST 2026, Created by josephlo

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.openqa.selenium.chrome.ChromeOptions;
import org.zkoss.test.webdriver.ExternalZkXml;
import org.zkoss.test.webdriver.ForkJVMTestOnly;
import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

@ForkJVMTestOnly
public class B104_ZK_5860Test extends WebDriverTestCase {
    @RegisterExtension
    public static final ExternalZkXml CONFIG = new ExternalZkXml("/test2/enable-tablet-ui-zk.xml");

    @Override
    protected ChromeOptions getWebDriverOptions() {
        return super.getWebDriverOptions()
                .setExperimentalOption("mobileEmulation", Collections
                        .singletonMap("deviceName", "iPad"));
    }

    @Test
    public void test() {
        connect();

        JQuery dbox1 = jq(".z-datebox").eq(0);
        click(dbox1.find(".z-datebox-button"));
        waitResponse();
        sleep(500);
        assertTrue(jq(".z-timebox-wheel-list-selected").exists());
        getEval("jq('.z-datebox-pp:visible .z-calendar-wheel-left').click()");
        waitResponse();
        sleep(500);
        getEval("jq('.z-button').eq(0).click()");
        waitResponse();
        sleep(500);
        getEval("jq('.z-datebox').eq(0).next('.z-button').click()");
        waitResponse();
        sleep(500);
        assertTrue(jq(".z-timebox-wheel-list-selected").exists());
        getEval("jq('.z-datebox-pp:visible .z-calendar-wheel-left').click()");
        waitResponse();
        sleep(500);

        JQuery dbox2 = jq(".z-datebox").eq(1);
        click(dbox2.find(".z-datebox-button"));
        waitResponse();
        sleep(500);
        assertTrue(jq(".z-timebox-wheel-list-selected").exists());
        getEval("jq('.z-datebox-pp:visible .z-calendar-wheel-left').click()");
        waitResponse();
        sleep(500);
        getEval("jq('.z-button').eq(1).click()");
        waitResponse();
        sleep(500);
        getEval("jq('.z-datebox').eq(1).next('.z-button').click()");
        waitResponse();
        sleep(500);
        assertTrue(jq(".z-timebox-wheel-list-selected").exists());
        getEval("jq('.z-datebox-pp:visible .z-calendar-wheel-left').click()");
        waitResponse();
    }
}
