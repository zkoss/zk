/* Tabbox_selectedTest.java

	Purpose:
		
	Description:
		
	History:
		Tue Apr 27 18:08:23 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.comp;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Tabbox;

/**
 * @author rudyhuang
 */
public class Tabbox_selectedTest extends ZATSTestCase {
	@Test
	public void testSingleWay() {
		final DesktopAgent desktop = connect();
		final List<ComponentAgent> listitems = desktop.queryAll("#listbox1 listitem");
		final Listbox listbox = desktop.query("#listbox1").as(Listbox.class);
		final Tabbox tabbox = desktop.query("#tabbox1").as(Tabbox.class);

		listitems.get(0).select();
		Assertions.assertEquals(0, tabbox.getSelectedIndex());
		listitems.get(1).select();
		Assertions.assertEquals(1, tabbox.getSelectedIndex());
		desktop.queryAll("#tabbox1 tab").get(2).select();
		Assertions.assertEquals(1, listbox.getSelectedIndex()); // Won't change
	}

	@Test
	public void testTwoWay() {
		final DesktopAgent desktop = connect();
		final List<ComponentAgent> listitems = desktop.queryAll("#listbox2 listitem");
		final Listbox listbox = desktop.query("#listbox2").as(Listbox.class);
		final Tabbox tabbox = desktop.query("#tabbox2").as(Tabbox.class);

		listitems.get(0).select();
		Assertions.assertEquals(0, tabbox.getSelectedIndex());
		listitems.get(1).select();
		Assertions.assertEquals(1, tabbox.getSelectedIndex());
		desktop.queryAll("#tabbox2 tab").get(2).select();
		Assertions.assertEquals(2, listbox.getSelectedIndex());
	}
}
