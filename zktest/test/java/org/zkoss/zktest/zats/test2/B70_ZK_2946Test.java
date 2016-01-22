/* B70_ZK_2946Test.java
	Purpose:

	Description:

	History:
		Mon Jan 20 16:36:18 CST 2016, Created by wenning
Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

import static org.junit.Assert.assertEquals;

/**
 * Created by wenning on 1/20/16.
 */
public class B70_ZK_2946Test extends WebDriverTestCase {

    @Test
    public void test() {
        connect();
        JQuery tcs = jq(".z-treecell");
        assertEquals("", tcs.get(0).eval("style.textAlign"));
        assertEquals("", tcs.get(3).eval("style.textAlign"));
        assertEquals("center", tcs.get(1).eval("style.textAlign"));
        assertEquals("center", tcs.get(4).eval("style.textAlign"));
        assertEquals("right", tcs.get(2).eval("style.textAlign"));
        assertEquals("right", tcs.get(5).eval("style.textAlign"));
    }

}
