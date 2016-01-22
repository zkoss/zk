/* B70_ZK_2891Test.java
	Purpose:

	Description:

	History:
		Mon Jan 21 16:36:18 CST 2016, Created by wenning
Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

import static org.junit.Assert.assertEquals;

/**
 * Created by wenning on 1/21/16.
 */
public class B70_ZK_2891Test extends WebDriverTestCase {

    @Test
    public void test() {
        connect();
        JQuery windows = jq(".z-window");
        assertEquals(windows.eq(0).offsetTop(), windows.eq(1).offsetTop());
    }

}
