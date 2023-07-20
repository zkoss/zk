/* F100_ZK_5215Test.java

	Purpose:
		
	Description:
		
	History:
		10:23 AM 2023/7/20, Created by jumperchen

Copyright (C) 2023 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;


import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
/**
 * @author jumperchen
 */
public class F100_ZK_5215Test extends ZATSTestCase {

	@Test
	public void test() {
		try {
			DesktopAgent desktop = connect();
		} catch (Throwable t) {
			fail("cannot run into this line");
		}
	}
}
