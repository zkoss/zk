/* B86_ZK_4053Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Sep 11 10:18:02 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Combobutton;

/**
 * @author rudyhuang
 */
public class B86_ZK_4053Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();

		Bandbox comp1 = desktop.query("bandbox").as(Bandbox.class);
		Combobox comp2 = desktop.query("combobox").as(Combobox.class);
		Combobutton comp3 = desktop.query("combobutton").as(Combobutton.class);

		desktop.queryAll("button[label$='open']").forEach(ComponentAgent::click);
		Assert.assertTrue("The bandbox should be opened.", comp1.isOpen());
		Assert.assertTrue("The combobox should be opened.", comp2.isOpen());
		Assert.assertTrue("The combobutton should be opened.", comp3.isOpen());

		desktop.queryAll("button[label$='close']").forEach(ComponentAgent::click);
		Assert.assertFalse("The bandbox should be closed.", comp1.isOpen());
		Assert.assertFalse("The combobox should be closed.", comp2.isOpen());
		Assert.assertFalse("The combobutton should be closed.", comp3.isOpen());
	}
}
