/* IMenubarTest.java

	Purpose:

	Description:

	History:
		Tue Oct 19 11:24:03 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.sul;

import org.junit.jupiter.api.Test;

import org.zkoss.stateless.mock.StatelessTestBase;
import org.zkoss.zul.Menubar;
import org.zkoss.zul.Menuitem;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test for {@link IMenubar}
 *
 * @author katherine
 */
public class IMenubarTest extends StatelessTestBase {
	@Test
	public void withMenubarbar() {
		// check Richlet API case
		assertEquals(richlet(() -> IMenubar.of(IMenuitem.of("abc"))), zul(IMenubarTest::newMenubar));

		// check Stateless file case
		assertEquals(composer(IMenubarTest::newMenubar), zul(IMenubarTest::newMenubar));

		// check Stateless file and then recreate another immutable case
		assertEquals(
				thenComposer(() -> {
					Menubar Menubar = new Menubar();
					Menubar.appendChild(new Menuitem("123"));
					return Menubar;
				}, (IMenubar iMenubar) -> iMenubar.withChildren(IMenuitem.of("abc"))),
				zul(IMenubarTest::newMenubar));
	}

	private static Menubar newMenubar() {
		Menubar menubar = new Menubar();
		menubar.appendChild(new Menuitem("abc"));
		return menubar;
	}
}