package org.zkoss.zktest.zats.test2;

import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

import static org.junit.Assert.assertEquals;

/**
 * Created by wenning on 5/17/16.
 */
public class B80_ZK_3210Test extends WebDriverTestCase {

    @Test
    public void test() {
        connect();
        focus(jq("@spinner input").get(0));
        waitResponse(true);
        type(jq("@spinner input").get(0), "1");
        waitResponse(true);
        assertEquals("should be between 5 and 10 (incl.)", jq(".z-errorbox-content").get(0).eval("innerHTML"));
        focus(jq("@spinner input").get(0));
        waitResponse(true);
        type(jq("@spinner input").get(0), "11");
        waitResponse(true);
        assertEquals("should be between 5 and 10 (incl.)", jq(".z-errorbox-content").get(0).eval("innerHTML"));
        focus(jq("@spinner input").get(1));
        waitResponse(true);
        type(jq("@spinner input").get(1), "1");
        waitResponse(true);
        assertEquals("min is 5", jq(".z-errorbox-content").get(1).eval("innerHTML"));
        focus(jq("@spinner input").get(1));
        waitResponse(true);
        type(jq("@spinner input").get(1), "11");
        waitResponse(true);
        assertEquals("max is 10", jq(".z-errorbox-content").get(1).eval("innerHTML"));
        focus(jq("@spinner input").get(2));
        waitResponse(true);
        focus(jq("@spinner input").get(1));
        waitResponse(true);
        assertEquals("This field may not be empty or contain only spaces.", jq(".z-errorbox-content").get(2).eval("innerHTML"));
    }
}
