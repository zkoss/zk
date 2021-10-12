/** FormWithNullTest.java.

	Purpose:
		
	Description:
		
	History:
		5:36:21 PM Feb 16, 2015, Created by jumperchen

Copyright (C) 2014 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.zuti.mvvm._apply;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.zuti.ZutiBasicTestCase;

/**
 * @author jumperchen
 *
 */
public class FormWithNullTest extends ZutiBasicTestCase {
	@Test
	public void test() {
		
		DesktopAgent desktop = connect();
		List<ComponentAgent> buttons = desktop.queryAll("button");
		assertEquals(2, buttons.size());
		try {
			buttons.get(0).click();
			buttons.get(1).click();
		} catch (Exception e) {
			fail("Cannot run into this line!");
		}
	}
}
