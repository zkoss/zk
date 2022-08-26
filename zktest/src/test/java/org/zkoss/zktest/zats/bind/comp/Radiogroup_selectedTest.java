/* Radiogroup_selectedTest.java

	Purpose:
		
	Description:
		
	History:
		Tue Apr 27 16:20:09 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.comp;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Radio;

/**
 * @author rudyhuang
 */
public class Radiogroup_selectedTest extends ZATSTestCase {
	@Test
	public void test() {
		final DesktopAgent desktop = connect();
		// All selected B
		Assertions.assertTrue(desktop.query("window #itemb").as(Listitem.class).isSelected());
		Assertions.assertTrue(desktop.query("window #radiob").as(Radio.class).isSelected());

		// Select Item A
		desktop.query("window #itema").select();
		Assertions.assertTrue(desktop.query("window #radioa").as(Radio.class).isSelected());

		// Select Item C
		desktop.query("window #radioc").check(true);
		Assertions.assertTrue(desktop.query("window #itemc").as(Listitem.class).isSelected());
	}
}
