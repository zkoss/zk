/* B70_ZK_2860Test.java
	Purpose:

	Description:

	History:
		Mon Jan 20 16:36:18 CST 2016, Created by wenning
Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.Element;
import org.zkoss.zktest.zats.ztl.JQuery;

import static org.junit.Assert.assertEquals;

/**
 * Created by wenning on 1/20/16.
 */
public class B70_ZK_2860Test extends WebDriverTestCase {

    @Test
    public void test() {
        connect();
        JQuery btns = jq("@button");
        JQuery rows = jq(".z-row");
        JQuery odd = jq(".z-grid-odd");
        Element row1 = rows.get(1);
        Element row2 = rows.get(2);
        click(btns.get(0));
        waitResponse();
        assertEquals(odd.get(1).eval("id"), row2.eval("id"));
        click(btns.get(1));
        waitResponse();
        assertEquals(1, odd.length());
        click(btns.get(2));
        waitResponse();
        assertEquals("true", widget(rows.get(0)).eval("_visible"));
        assertEquals("true", widget(row1).eval("_visible"));
        assertEquals("true", widget(row2).eval("_visible"));
        assertEquals(1, odd.length());
        assertEquals(odd.get(0).eval("id"), row1.eval("id"));
    }

}
