/* IMenuTest.java

	Purpose:

	Description:

	History:
		Fri Oct 15 19:03:38 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.zpr;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.zephyr.mock.ZephyrTestBase;
import org.zkoss.zul.Menu;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Menupopup;
import org.zkoss.zul.Menuseparator;

/**
 * Test for {@link IMenu}
 *
 * @author katherine
 */
public class IMenuTest extends ZephyrTestBase {
	@Test
	public void withMenu() {
		IMenupopup iMenupopup = IMenupopup.of(IMenuitem.of("abc"), IMenuseparator.DEFAULT);
		// check Richlet API case
		assertEquals(richlet(() -> IMenu.of(iMenupopup)), zul(IMenuTest::newMenu));

		// check Zephyr file case
		assertEquals(composer(IMenuTest::newMenu), zul(IMenuTest::newMenu));

		// check Zephyr file and then recreate another immutable case
		assertEquals(
				thenComposer(() -> {
					Menu menu = new Menu();
					Menupopup menupopup = new Menupopup();
					menupopup.appendChild(new Menuitem("123"));
					menupopup.appendChild(new Menuseparator());
					menu.appendChild(menupopup);
					return menu;
				}, (IMenu iMenu) -> iMenu.withChild(iMenupopup)),
				zul(IMenuTest::newMenu));
	}

	private static Menu newMenu() {
		Menu menu = new Menu();
		Menupopup menupopup = new Menupopup();
		menupopup.appendChild(new Menuitem("abc"));
		menupopup.appendChild(new Menuseparator());
		menu.appendChild(menupopup);
		return menu;
	}
}