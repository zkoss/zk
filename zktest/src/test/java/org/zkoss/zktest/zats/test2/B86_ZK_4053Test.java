/* B86_ZK_4053Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Sep 11 10:18:02 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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
		Assertions.assertTrue(comp1.isOpen(), "The bandbox should be opened.");
		Assertions.assertTrue(comp2.isOpen(), "The combobox should be opened.");
		Assertions.assertTrue(comp3.isOpen(),
				"The combobutton should be opened.");

		desktop.queryAll("button[label$='close']").forEach(ComponentAgent::click);
		Assertions.assertFalse(comp1.isOpen(), "The bandbox should be closed.");
		Assertions.assertFalse(comp2.isOpen(), "The combobox should be closed.");
		Assertions.assertFalse(comp3.isOpen(),
				"The combobutton should be closed.");
	}
}
