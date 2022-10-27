/* IWindowTest.java

	Purpose:

	Description:

	History:
		Thu Oct 21 17:05:57 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.sul;

import org.junit.jupiter.api.Test;

import org.zkoss.stateless.mock.StatelessTestBase;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zul.Window;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test for {@link IWindow}
 *
 * @author katherine
 */
public class IWindowTest extends StatelessTestBase {
	@Test
	public void withWindow() {
		// check Richlet API case
		assertEquals(richlet(() -> IWindow.ofTitle("abc").withContentSclass("class")), zul(IWindowTest::newWindow));

		// check Stateless file case
		assertEquals(composer(IWindowTest::newWindow), zul(IWindowTest::newWindow));

		// check Stateless file and then recreate another immutable case
		assertEquals(
				thenComposer(() -> {
					Window window = new Window();
					window.setTitle("123");
					window.setContentSclass("class");
					return window;
				}, (IWindow iWindow) -> iWindow.withTitle("abc").withContentSclass("class")),
				zul(IWindowTest::newWindow));
	}

	@Test
	public void withMode() {
		String mode = "abc";
		try {
			IWindow.ofTitle("abc").withMold(mode);
		} catch (UiException ex) {
			assertEquals("Unknown mode: " + mode, ex.getMessage());
		}
	}

	@Test
	public void withDraggable() {
		try {
			IWindow.ofTitle("abc").withMold("highlighted").withDraggable("true");
		} catch (UiException ex) {
			assertEquals("Only embedded window could be draggable", ex.getMessage());
		}
	}

	private static Window newWindow() {
		Window window = new Window();
		window.setTitle("abc");
		window.setContentSclass("class");
		return window;
	}
}