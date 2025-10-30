/* B80_ZK_3546Test.java

	Purpose:

	Description:

	History:
		Mon Mar 12 16:34:31 CST 2017, Created by christopher

Copyright (C) 2017 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;



/**
 * 
 * @author christopher
 */
public class B80_ZK_3546Test extends ZATSTestCase {
    @Test
    public void test(){
		DesktopAgent desktop = connect();
		// check init condition
		assertEquals(7, desktop.queryAll("label").size(), "Expecting 7 labels at init");
		assertEquals(4, desktop.queryAll("button").size(), "Expecting 4 buttons at init");
		// recreate templates 3 times
		desktop.query("#t1").click();
		desktop.query("#t1").click();
		desktop.query("#t1").click();
		// everything should look the same
		assertEquals(7, desktop.queryAll("label").size(), "Expecting 7 labels after recreate");
		assertEquals(4, desktop.queryAll("button").size(), "Expecting 4 buttons after recreate");
		// add 3 items to the model
		desktop.query("#add").click();
		desktop.query("#add").click();
		desktop.query("#add").click();
		// check final condition
		assertEquals(31, desktop.queryAll("label").size(), "Expecting 31 labels after lots of clicking");
		assertEquals(7, desktop.queryAll("button").size(), "Expecting 7 buttons after lots of clicking");
    }
}
