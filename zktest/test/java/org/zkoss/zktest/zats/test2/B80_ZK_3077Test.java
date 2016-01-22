/* B80_ZK_3077Test.java
	Purpose:

	Description:

	History:
		Mon Jan 18 16:36:18 CST 2016, Created by wenning
Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

import static org.junit.Assert.assertEquals;

/**
 * Created by wenning on 1/18/16.
 */
public class B80_ZK_3077Test extends WebDriverTestCase {

    @Test
    public void test() {
        connect();
        JQuery listitems = jq("@listitem");
        JQuery listitemfocus = jq(".z-listitem-focus");
        JQuery lii = listitems.find("i");
        for (int i = 0; i < 4; i++) {
            click(lii.get(i));
            waitResponse();
        }
        for (int i = 0; i < 3; i++) {
            click(lii.get(i));
            waitResponse();
        }
        assertEquals(1, listitemfocus.length());
        assertEquals(listitems.get(2).get("id"), listitemfocus.get(0).get("id"));
    }

}
