package org.zkoss.zktest.zats.test2;

import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

import static org.junit.Assert.assertEquals;

/**
 * Created by wenninghsu on 6/27/16.
 */
public class B80_ZK_3236Test extends WebDriverTestCase {

    @Test
    public void test() {
        connect();
        click(jq(".z-caption-content span"));
        waitResponse(true);
        click(jq(".z-groupbox-title-content"));
        waitResponse(true);
        click(jq("@button").get(0));
        waitResponse(true);
        click(jq("@button").get(1));
        waitResponse(true);
        assertEquals("true true", getZKLog());
    }

}
