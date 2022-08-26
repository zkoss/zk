/* B70_ZK_2841Test.java
	Purpose:

	Description:

	History:
		Mon Jan 19 16:36:18 CST 2016, Created by wenning
Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * Created by wenning on 1/19/16.
 */
public class B70_ZK_2841Test extends WebDriverTestCase {

    @Test
    public void test() {
        connect();
        JQuery buttons = jq("@button");
        JQuery nis = jq(".z-navitem-selected");
        click(buttons.get(0));
        waitResponse();
        assertEquals(jq("@navitem").get(0).get("id"), nis.get(0).get("id"));
        click(buttons.get(1));
        waitResponse();
        assertFalse(nis.exists());
    }

}
