/* B80_ZK_2930Test.java

	Purpose:
		
	Description:
		
	History:
		Wed, Nov 11, 2015 11:30:58 AM, Created by JamesChu

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Test;
import org.zkoss.zats.ZatsException;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;

import static org.junit.Assert.fail;

/**
 * @author jameschu
 */
public class B80_ZK_2930Test extends ZATSTestCase {
	@Test
	public void test() {
		try {
			DesktopAgent desktop = connect();
			ComponentAgent btn = desktop.query("#btn");
			btn.click();
		} catch(ZatsException e) {
			fail();
		}
	}
}