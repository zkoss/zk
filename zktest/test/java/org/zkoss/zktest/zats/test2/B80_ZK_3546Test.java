/* B80_ZK_3546Test.java

	Purpose:

	Description:

	History:
		Mon Mar 12 16:34:31 CST 2017, Created by christopher

Copyright (C) 2017 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Test;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;

import static org.junit.Assert.assertEquals;

/**
 * 
 * @author christopher
 */
public class B80_ZK_3546Test extends ZATSTestCase {
    @Test
    public void test(){
		DesktopAgent desktop = connect();
		// check init condition
		assertEquals("Expecting 4 labels at init", 4, desktop.queryAll("label").size());
		assertEquals("Expecting 3 buttons at init", 3, desktop.queryAll("button").size());
		// switch templates 3 times
		desktop.query("#t2").click();
		desktop.query("#t1").click();
		desktop.query("#t2").click();
		// add 3 items to the model
		desktop.query("#add").click();
		desktop.query("#add").click();
		desktop.query("#add").click();
		// check final condition
		assertEquals("Expecting 0 labels after lots of clicking", 0, desktop.queryAll("label").size());
		assertEquals("Expecting 10 buttons after lots of clicking", 10, desktop.queryAll("button").size());
    }
}
