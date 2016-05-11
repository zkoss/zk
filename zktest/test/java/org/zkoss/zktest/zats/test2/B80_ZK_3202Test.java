package org.zkoss.zktest.zats.test2;

import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

import static org.junit.Assert.assertEquals;

/**
 * Created by wenning on 5/11/16.
 */
public class B80_ZK_3202Test extends WebDriverTestCase {

    @Test
    public void test() {
        connect();
        focus(jq("@spinner input").get(0));
        waitResponse(true);
        type(jq("@spinner input").get(0), "4");
        waitResponse(true);
        assertEquals("custom error", jq(".z-errorbox-content").get(0).eval("innerHTML") );
        focus(jq("@spinner input").get(0));
        waitResponse(true);
        type(jq("@spinner input").get(0), "10");
        waitResponse(true);
        assertEquals("max is 9", jq(".z-errorbox-content").get(0).eval("innerHTML") );
        focus(jq("@spinner input").get(1));
        waitResponse(true);
        type(jq("@spinner input").get(1), "4");
        waitResponse(true);
        assertEquals("Out of range: &gt;= 5", jq(".z-errorbox-content").get(1).eval("innerHTML") );
    }
}
