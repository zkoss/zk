/* B80_ZK_2789Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Aug 14 12:40:11 CST 2015, Created by Christopher

Copyright (C)  Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Combobox;

/**
 * 
 * @author Christopher
 */
public class B80_ZK_2789Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		ComponentAgent comboAgent = desktop.query("#cb1");
		Combobox combobox = comboAgent.as(Combobox.class);
		Assert.assertEquals("None", combobox.getItemAtIndex(0).getLabel());
	}
}
