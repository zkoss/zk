/* F80_ZK_2584Test.java
	Purpose:

	Description:

	History:
		Fri Dec 18 16:42:35 CST 2015, Created by wenning
Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;
import org.zkoss.zktest.zats.ztl.Widget;

/**
 * Created by wenning on 12/18/15.
 */
public class F80_ZK_2993Test extends WebDriverTestCase {
    @Test
    public void test() {
        connect();
        Widget textbox1 = widget("$t1");
        Widget textbox2 = widget("$t2");
        type(textbox1, "test1m");
        waitResponse();
        assertEquals("test1m", jq("$l1").text());
        type(textbox2, "test2m");
        assertEquals("test2", jq("$l2").text());
        click(jq("$btn"));
        waitResponse();
        assertEquals("test2m", jq("$l2").text());
    }
}
