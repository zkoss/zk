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
		assertEquals("Expecting 7 labels at init", 7, desktop.queryAll("label").size());
		assertEquals("Expecting 4 buttons at init", 4, desktop.queryAll("button").size());
		// recreate templates 3 times
		desktop.query("#t1").click();
		desktop.query("#t1").click();
		desktop.query("#t1").click();
		// everything should look the same
		assertEquals("Expecting 7 labels after recreate", 7, desktop.queryAll("label").size());
		assertEquals("Expecting 4 buttons after recreate", 4, desktop.queryAll("button").size());
		// add 3 items to the model
		desktop.query("#add").click();
		desktop.query("#add").click();
		desktop.query("#add").click();
		// check final condition
		assertEquals("Expecting 31 labels after lots of clicking", 31, desktop.queryAll("label").size());
		assertEquals("Expecting 7 buttons after lots of clicking", 7, desktop.queryAll("button").size());
    }
}
