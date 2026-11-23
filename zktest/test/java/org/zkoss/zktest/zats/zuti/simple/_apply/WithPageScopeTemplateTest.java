/** WithPageScopeTemplateTest.java.

	Purpose:
		
	Description:
		
	History:
		3:41:54 PM Feb 10, 2015, Created by jameschu

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.zuti.simple._apply;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.zuti.ZutiBasicTestCase;

/**
 * @author jameschu
 *
 */
public class WithPageScopeTemplateTest extends ZutiBasicTestCase {

	@Test
	public void test() {
		DesktopAgent desktop = connect();
		ComponentAgent a1 = desktop.query("#a1");
		ComponentAgent a2 = desktop.query("#a2");
		ComponentAgent a3 = desktop.query("#a3");
		assertTrue(a1.getChildren().size() == 1);
		assertTrue(a2.getChildren().size() == 3);
		assertTrue(a3.getChildren().size() == 3);
	}
}
