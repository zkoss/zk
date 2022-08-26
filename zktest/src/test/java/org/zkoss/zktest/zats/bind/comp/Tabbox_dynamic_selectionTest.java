/* Tabbox_dynamic_selectionTest.java

	Purpose:
		
	Description:
		
	History:
		Tue Apr 27 17:58:07 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.comp;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

/**
 * @author rudyhuang
 */
public class Tabbox_dynamic_selectionTest extends ZATSTestCase {
	@Test
	public void testIndex() {
		final DesktopAgent desktop = connect();
		final List<ComponentAgent> buttons = desktop.queryAll("#vbox1 button");
		final Label selectedIndex = desktop.query("#selectedIndex").as(Label.class);

		buttons.get(0).click();
		Assertions.assertEquals("1", selectedIndex.getValue());
		buttons.get(1).click();
		Assertions.assertEquals("3", selectedIndex.getValue());

		desktop.queryAll("#vbox1 tab").get(4).select();
		Assertions.assertEquals("4", selectedIndex.getValue());
	}

	@Test
	public void testItem() {
		final DesktopAgent desktop = connect();
		final List<ComponentAgent> buttons = desktop.queryAll("#vbox2 button");
		final Label selectedItem = desktop.query("#selectedItem").as(Label.class);

		buttons.get(0).click();
		Assertions.assertEquals("Tab 1", selectedItem.getValue());
		buttons.get(1).click();
		Assertions.assertEquals("Tab 3", selectedItem.getValue());

		desktop.queryAll("#vbox2 tab").get(4).select();
		Assertions.assertEquals("Tab 4", selectedItem.getValue());
	}
}
