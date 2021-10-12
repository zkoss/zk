/* F80_ZK_2950Test.java

	Purpose:
		
	Description:
		
	History:
		9:51 AM 11/11/15, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.zuti.ZutiBasicTestCase;

/**
 * @author jumperchen
 */
public class F80_ZK_2950Test extends ZutiBasicTestCase {

	@Test
	public void testPersistentShadowElement() {
		DesktopAgent desktop = connect(getTestURL("F80-ZK-2950.zul"));
		ComponentAgent windowAgent = desktop.query("window");
		assertEquals(1, getShadowSize(windowAgent));
	}
}
