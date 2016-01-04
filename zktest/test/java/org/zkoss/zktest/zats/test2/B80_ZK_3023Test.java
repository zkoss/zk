/* F80_ZK_3023Test.java

	Purpose:

	Description:

	History:
		Mon Jan 04 14:25:42 CST 2016, Created by wenning

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.zktest.zats.test2;

import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

import static org.junit.Assert.assertEquals;

/**
 * Created by wenning on 1/4/16.
 */
public class B80_ZK_3023Test extends WebDriverTestCase {
    @Test
    public void test() {
        connect();
        click(jq(".z-bandbox-button"));
        waitResponse();
        assertEquals(306, jq(".z-bandbox-popup").width());
    }
}
