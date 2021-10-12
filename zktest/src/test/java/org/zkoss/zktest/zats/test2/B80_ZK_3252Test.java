package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * Created by wenning on 7/1/16.
 */
public class B80_ZK_3252Test extends WebDriverTestCase {

    @Test
    public void test() {
        connect();
        click(jq("@intbox").get(0));
        waitResponse(true);
        click(jq("@div").get(0));
        waitResponse(true);
        assertEquals(jq("@intbox").offsetTop() + jq("@intbox").outerHeight(), jq("@errorbox").offsetTop(), 1);
        assertEquals(jq("@intbox").offsetLeft(), jq("@errorbox").offsetLeft());
    }
}
