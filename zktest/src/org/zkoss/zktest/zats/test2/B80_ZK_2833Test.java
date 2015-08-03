/* B80_ZK_2833Test.java

	Purpose:
		
	Description:
		
	History:
		9:35 AM 8/3/15, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.fail;

import org.junit.Test;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;

/**
 * @author jumperchen
 */
public class B80_ZK_2833Test extends ZATSTestCase {

	@Test
	public void test() {
		try {
			DesktopAgent desktop = connect();
		} catch (Exception e) {
			fail("No exception here");
		}
	}
}
