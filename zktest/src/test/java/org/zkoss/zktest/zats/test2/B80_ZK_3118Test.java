/* B80_ZK_3118Test.java
	Purpose:

	Description:

	History:
		Tue Apr 8 18:12:46 CST 2016, Created by wenninghsu
Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

import static org.junit.Assert.assertEquals;

/**
 * Created by wenning on 4/8/16.
 */
public class B80_ZK_3118Test extends WebDriverTestCase {

    @Test
    public void test() {
        connect();
        assertEquals(0, jq(".z-apply-mask").length());
        click(jq("@button").get(0));
        waitResponse(true);
        assertEquals(1, jq(".z-apply-mask").length());
        click(jq("@tab").get(1));
        waitResponse(true);
        assertEquals(2, jq(".z-apply-mask").length());

    }
}
