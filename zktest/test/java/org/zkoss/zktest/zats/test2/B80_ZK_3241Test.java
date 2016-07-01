/* B80_ZK_3241Test.java
	Purpose:

	Description:

	History:
Copyright (C) 2016 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

/**
 * Created by jameschu
 */
public class B80_ZK_3241Test extends WebDriverTestCase {

    @Test
    public void test() {
        connect();
		click(jq("@button").first());
		waitResponse();
		assertEquals(0, jq(".z-errorbox").length());
		click(jq("@button").last());
		waitResponse();
		assertEquals(0, jq(".z-errorbox").length());
    }
}
