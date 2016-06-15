package org.zkoss.zktest.zats.test2;

import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

import static org.junit.Assert.assertEquals;

/**
 * Created by wenning on 6/15/16.
 */
public class F80_ZK_2959Test extends WebDriverTestCase {

    @Test
    public void test() {
        connect();
        click(jq("@button").get(0));
        waitResponse(true);
        System.out.println(getZKLog());
        assertEquals("image/svg+xml(F80-ZK-2959); charset=UTF-8", getZKLog());
    }
}
