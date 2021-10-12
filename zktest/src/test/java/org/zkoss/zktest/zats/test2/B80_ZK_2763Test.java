/* B80_ZK_2763Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Oct 28 10:11:57 CST 2015, Created by christopher

Copyright (C)  2015 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

/**
 * 
 * @author christopher
 */
public class B80_ZK_2763Test extends ZATSTestCase {
	@Test
	public void test() {
		try {
			DesktopAgent desktop = connect();
			List<ComponentAgent> labels = desktop.queryAll("label");
			List<String> oldValues = new ArrayList<String>();
			for (ComponentAgent label : labels) {
				oldValues.add(label.as(Label.class).getValue());
			}
			System.out.println(oldValues.size());
			oldValues.remove(0); //first label is the description
			List<ComponentAgent> buttons = desktop.queryAll("button");
			for (ComponentAgent btn : buttons) {
				btn.click();
			}
			List<ComponentAgent> labelsAfterClick = desktop.queryAll("label");
			//skip the first label again
			Assert.assertFalse(oldValues.get(0).equals(labelsAfterClick.get(1).as(Label.class).getValue()));
			Assert.assertFalse(oldValues.get(1).equals(labelsAfterClick.get(2).as(Label.class).getValue()));
			Assert.assertFalse(oldValues.get(2).equals(labelsAfterClick.get(3).as(Label.class).getValue()));
			Assert.assertTrue(oldValues.get(3).equals(labelsAfterClick.get(4).as(Label.class).getValue()));
		} catch (Exception e) {
			fail();
		}
	}
}

