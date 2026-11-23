/* B80_ZK_2823Test.java

	Purpose:
		
	Description:
		
	History:
		12:42 PM 7/21/15, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.*;

import org.junit.Test;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;

/**
 * @author jumperchen
 */
public class B80_ZK_2823Test extends ZATSTestCase {

	@Test
	public void test() {
		DesktopAgent desktop = connect();
		try {
			desktop.query("button").click();
		} catch (Exception e) {
			fail("No exception here");
		}
	}

}
