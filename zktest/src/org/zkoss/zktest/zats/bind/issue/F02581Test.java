/* F02581Test.java

	Purpose:
		
	Description:
		
	History:
		Wed, Jan 07, 2015  5:26:43 PM, Created by jumperchen

Copyright (C)  Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zktest.zats.bind.issue;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

/**
 * 
 * @author jumperchen
 */
public class F02581Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		
		ComponentAgent l1 = desktop.query("#inc1 #win2 #l1");
		ComponentAgent l2 = desktop.query("#inc1 #win2 #l2");
		ComponentAgent reload = desktop.query("#reload");
		
		String val1;
		String val2;
		
		for (int i = 0; i < 4; i++) {
			val1 = l1.as(Label.class).getValue();
			val2 = l2.as(Label.class).getValue();
			reload.click();
			l1 = desktop.query("#inc1 #win2 #l1");
			l2 = desktop.query("#inc1 #win2 #l2");
			assertTrue(val1.equals(l1.as(Label.class).getValue()));
			assertTrue(val2.equals(l2.as(Label.class).getValue()));
	
			try {
				Thread.sleep(100);
			} catch (Exception e) {
				
			}
		}
	}
	

	@Test
	public void testChange() {
		DesktopAgent desktop = connect();
		
		ComponentAgent l1 = desktop.query("#inc1 #win2 #l1");
		ComponentAgent l2 = desktop.query("#inc1 #win2 #l2");
		ComponentAgent reload = desktop.query("#reload2");
		
		String val1;
		String val2;
		
		for (int i = 0; i < 4; i++) {
			val1 = l1.as(Label.class).getValue();
			val2 = l2.as(Label.class).getValue();
			reload.click();
			l1 = desktop.query("#inc1 #win2 #l1");
			l2 = desktop.query("#inc1 #win2 #l2");
			assertFalse(val1.equals(l1.as(Label.class).getValue()));
			assertFalse(val2.equals(l2.as(Label.class).getValue()));
	
			try {
				Thread.sleep(100);
			} catch (Exception e) {
				
			}
		}
	}
}