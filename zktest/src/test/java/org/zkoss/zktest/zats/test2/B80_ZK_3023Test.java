/* F80_ZK_3023Test.java

	Purpose:

	Description:

	History:
		Mon Jan 04 14:25:42 CST 2016, Created by wenning

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;



/**
 * Created by wenning on 1/4/16.
 */
public class B80_ZK_3023Test extends WebDriverTestCase {
    @Test
    public void test() {
        connect();
        click(jq(".z-bandbox-button"));
        waitResponse();
        assertEquals(300, jq(".z-bandbox-popup").width());
    }
}
