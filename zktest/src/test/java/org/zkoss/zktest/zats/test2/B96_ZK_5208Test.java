/* B96_ZK_5208Test.java

	Purpose:
		
	Description:
		
	History:
		2:46 PM 2022/10/7, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;

/**
 * @author jumperchen
 */
public class B96_ZK_5208Test extends ZATSTestCase {

	@Test
	public void test() {
		try {
			DesktopAgent desktop = connect();
		} catch (Throwable t) {
			fail("cannot run into this line");
		}
	}
}
