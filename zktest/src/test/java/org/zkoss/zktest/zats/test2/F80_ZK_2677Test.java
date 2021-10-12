/* F80_ZK_2677Test.java

	Purpose:
		
	Description:
		
	History:
		Fri, Jun  3, 2016  5:58:56 PM, Created by Sefi

Copyright (C)  Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

import static junit.framework.Assert.assertTrue;

/**
 * 
 * @author Sefi
 */
public class F80_ZK_2677Test extends WebDriverTestCase {
    @Test
    public void testZK2677() {
        connect();
        waitResponse();
        JQuery errorboxes = jq(".z-errorbox");
        assertTrue(errorboxes.eq(0).find(".z-errorbox-icon").hasClass("z-icon-asterisk"));
        assertTrue(errorboxes.eq(1).find(".z-errorbox-icon").hasClass("z-icon-bus"));
        assertTrue(errorboxes.eq(2).find(".z-errorbox-icon").hasClass("z-icon-cutlery"));
        assertTrue(errorboxes.eq(3).find(".z-errorbox-icon").hasClass("z-icon-envelope"));
        assertTrue(errorboxes.eq(4).find(".z-errorbox-icon").hasClass("z-icon-eye"));
        assertTrue(errorboxes.eq(5).find(".z-errorbox-icon").hasClass("z-icon-home"));
    }
}
