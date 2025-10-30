/* F90_ZK_4347_errorsTest.java

	Purpose:
		
	Description:
		
	History:
		Tue Aug 13 15:41:30 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.zats.ZatsException;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;

/**
 * @author rudyhuang
 */
public class F90_ZK_4347_errorsTest extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();

		final List<ComponentAgent> buttons = desktop.queryAll("button");
		testSetter(buttons.get(0));
		testSetter(buttons.get(1));
		testSetter(buttons.get(2));
	}

	private void testSetter(ComponentAgent btn) {
		try {
			btn.click();
			Assertions.fail("Expected an UnsupportedOperationException");
		} catch (ZatsException expected) {
		}
	}
}
