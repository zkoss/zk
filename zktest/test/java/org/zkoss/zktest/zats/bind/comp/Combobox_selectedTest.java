/* Combobox_selectedTest.java

	Purpose:
		
	Description:
		
	History:
		Tue Apr 27 11:04:23 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.comp;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Listbox;

/**
 * @author rudyhuang
 */
public class Combobox_selectedTest extends ZATSTestCase {
	@Test
	public void test() {
		final DesktopAgent desktop = connect();
		final Listbox listbox = desktop.query("listbox").as(Listbox.class);
		final Combobox combobox = desktop.query("combobox").as(Combobox.class);
		Assert.assertEquals(1, listbox.getSelectedIndex());
		Assert.assertEquals(1, combobox.getSelectedIndex());

		desktop.queryAll("listbox > listitem").get(0).select();
		Assert.assertEquals(0, combobox.getSelectedIndex());

		desktop.queryAll("combobox > comboitem").get(2).select();
		Assert.assertEquals(2, listbox.getSelectedIndex());
	}
}
