/* IToolbarTest.java

	Purpose:

	Description:

	History:
		Fri Oct 22 11:13:52 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.zpr;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.zephyr.mock.ZephyrTestBase;
import org.zkoss.zul.Toolbar;
import org.zkoss.zul.Toolbarbutton;

/**
 * Test for {@link IToolbar}
 *
 * @author katherine
 */
public class IToolbarTest extends ZephyrTestBase {
	@Test
	public void withToolbar() {
		// check Richlet API case
		assertEquals(richlet(() -> IToolbar.of(IToolbarbutton.of("abc")).withAlign("center")), zul(IToolbarTest::newToolbar));

		// check Zephyr file case
		assertEquals(composer(IToolbarTest::newToolbar), zul(IToolbarTest::newToolbar));

		// check Zephyr file and then recreate another immutable case
		assertEquals(
				thenComposer(() -> {
					Toolbar toolbar = new Toolbar();
					toolbar.setAlign("end");
					toolbar.appendChild(new Toolbarbutton("abc"));
					return toolbar;
				}, (IToolbar<IAnyGroup> iToolbar) -> iToolbar.withChildren(IToolbarbutton.of("abc")).withAlign("center")),
				zul(IToolbarTest::newToolbar));
	}

	private static Toolbar newToolbar() {
		Toolbar toolbar = new Toolbar();
		toolbar.setAlign("center");
		toolbar.appendChild(new Toolbarbutton("abc"));
		return toolbar;
	}
}