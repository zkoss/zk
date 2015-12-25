/* B80_ZK_2999Test.java
	Purpose:

	Description:

	History:
		Fri Dec 23 14:33:47 CST 2015, Created by wenning
Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

import static org.junit.Assert.assertTrue;

/**
 * Created by wenning on 12/22/15.
 */
public class B80_ZK_2999Test extends WebDriverTestCase {
    @Test
    public void test() {
        connect();
        JQuery icon1 = jq(".z-icon-caret-right");
        click(icon1);
        check(jq(".z-checkbox").find("input"));
        waitResponse();
        JQuery trs = jq(".z-treerow-selected");
        assertTrue(trs.length() == 5);
    }
}
