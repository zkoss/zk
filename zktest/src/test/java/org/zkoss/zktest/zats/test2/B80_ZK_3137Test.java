/* B80_ZK_3137Test.java

	Purpose:

	Description:

	History:
		Wed, Jun 08, 2016  9:25:37 AM, Created by Christopher

Copyright (C) 2016 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

import static org.junit.Assert.assertEquals;

import java.util.Iterator;

/**
 * @author Christopher
 */
public class B80_ZK_3137Test extends WebDriverTestCase {

    @Test
    public void test() {
        connect();
        
        Iterator<JQuery> iter = jq(".z-menupopup [class^=\"z-icon\"]").iterator();
        
        while(iter.hasNext()) {
        	JQuery item = iter.next();
        	assertEquals("center", item.css("text-align"));
        	assertEquals("inline-block", item.css("display"));
        	assertEquals("16px", item.css("min-width"));
        	assertEquals("9px", item.css("margin-right"));
        }
    }
}
