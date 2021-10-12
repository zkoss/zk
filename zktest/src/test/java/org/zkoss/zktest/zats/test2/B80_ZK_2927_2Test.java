/* B80_ZK_2927Test.java

	Purpose:
		
	Description:
		
	History:
		11:38 AM 10/21/15, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.fail;

import org.junit.Test;
import org.zkoss.zats.ZatsException;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;

/**
 * @author jumperchen
 */
public class B80_ZK_2927_2Test extends ZATSTestCase {
	@Test
	public void test() {
		try {
			DesktopAgent desktopAgent = connect();
			ComponentAgent button = desktopAgent.query("button");

			for (int i = 0; i < 3; i++)
				button.click();

		} catch(ZatsException e) {
			fail();
		}
	}
}