package org.zkoss.zktest.zats.test2;

import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

import static org.junit.Assert.assertEquals;

/**
 * Created by wenninghsu on 7/1/16.
 */
public class B80_ZK_3253Test extends WebDriverTestCase {

    @Test
    public void test() {
        connect();
        click(jq("@button").get(0));
        waitResponse(true);
        System.out.println(getZKLog());
        assertEquals("0 null", getZKLog());
    }

}
