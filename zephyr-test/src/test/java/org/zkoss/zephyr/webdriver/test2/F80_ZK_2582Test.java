package org.zkoss.zephyr.webdriver.test2;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;

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
        int max = Integer.MAX_VALUE;
        for (String s : getZKLog().split("\n")) {
            Integer integer = Integer.valueOf(s);
            if (integer != 0) {
                assertThat(max, greaterThan(integer));
                max = integer;
            }
        }
    }

}
