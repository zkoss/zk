package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * Created by wenning on 5/5/16.
 */
public class F80_ZK_2582Test extends WebDriverTestCase {

    @Test
    public void test() {
        connect();
        JQuery $textbox = jq("@textbox");
        click($textbox);
        waitResponse();
        sendKeys($textbox, "222");
        waitResponse();
        click(jq(".z-page"));
        waitResponse();
        assertEquals("100\n99\n0\n-100", getZKLog());
    }

}
