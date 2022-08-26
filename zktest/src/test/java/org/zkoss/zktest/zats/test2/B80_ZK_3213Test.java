package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;



/**
 * Created by wenninghsu on 6/30/16.
 */
public class B80_ZK_3213Test extends WebDriverTestCase {

    @Test
    public void test() {
        connect();
        click(jq("@button").get(0));
        waitResponse(true);
        assertEquals("null", getZKLog());
    }

}
