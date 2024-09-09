/* B101_ZK_5677Test.java

	Purpose:

	Description:

	History:
		10:54 AM 2024/9/9, Created by jumperchen

Copyright (C) 2024 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import org.zkoss.zats.ZatsException;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.test2.B101_ZK_5677_Composer;
import org.zkoss.zktest.zats.ZATSTestCase;

/**
 * @author jumperchen
 */
public class B101_ZK_5677Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		ComponentAgent btn = desktop.query("#btn");
		try {
			btn.click();
			fail("cannot reach here");
		} catch (ZatsException ze) {
			assertEquals(1, B101_ZK_5677_Composer.errorCount, "error count should be 1");
		}
	}
}
