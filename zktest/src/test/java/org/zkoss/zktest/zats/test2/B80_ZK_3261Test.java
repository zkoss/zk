package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;



/**
 * Created by wenninghsu on 7/28/16.
 */
public class B80_ZK_3261Test extends WebDriverTestCase {

    @Test
    public void test() {
        connect();
        click(jq("@button").get(0));
        waitResponse(true);
        click(jq("@button").get(1));
        waitResponse(true);
        assertEquals(" hide panel3 show panel3 hide panel2", jq("$lb").text());
    }

}
