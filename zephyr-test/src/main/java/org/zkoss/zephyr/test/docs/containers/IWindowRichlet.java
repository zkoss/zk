/* IWindowRichlet.java

	Purpose:

	Description:

	History:
		11:26 AM 2022/3/4, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.test.docs.containers;

import org.zkoss.stateless.annotation.RichletMapping;
import org.zkoss.stateless.ui.StatelessRichlet;
import org.zkoss.stateless.zpr.IComponent;
import org.zkoss.stateless.zpr.ILabel;
import org.zkoss.stateless.zpr.IWindow;

/**
 * A set of example for {@link IWindow} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Containers/Window">IWindow</a>,
 * if any.
 *
 * @author jumperchen
 * @see IWindow
 */
@RichletMapping("/containers/iwindow")
public class IWindowRichlet implements StatelessRichlet {
	@RichletMapping("/example/embedded")
	public IComponent embedded() {
		return IWindow.ofTitle("Embedded Style")
				.withBorder(IWindow.Border.NORMAL).withWidth("300px")
				.withChildren(ILabel.of("Hello, Embedded!"));

	}

	@RichletMapping("/example/overlapped")
	public IComponent overlapped() {
		return IWindow.ofTitle("Overlapped Style")
				.withMode(IWindow.Mode.OVERLAPPED)
				.withBorder(IWindow.Border.NORMAL).withWidth("300px")
				.withChildren(ILabel.of("Hello, Overlapped!"));

	}

	@RichletMapping("/example/popup")
	public IComponent popup() {
		return IWindow.ofTitle("Popup Style").withMode(IWindow.Mode.POPUP)
				.withBorder(IWindow.Border.NORMAL).withWidth("300px")
				.withChildren(ILabel.of("Hello, Popup!"));
	}

	@RichletMapping("/example/modal")
	public IComponent modal() {
		return IWindow.ofTitle("Modal Style").withMode(IWindow.Mode.MODAL)
				.withBorder(IWindow.Border.NORMAL).withWidth("300px")
				.withChildren(ILabel.of("Hello, Modal!"));
	}

	@RichletMapping("/example/highlighted")
	public IComponent highlighted() {
		return IWindow.ofTitle("Highlight Style").withMode(IWindow.Mode.HIGHLIGHTED)
				.withBorder(IWindow.Border.NORMAL).withWidth("300px")
				.withChildren(ILabel.of("Hello, Highlight!"));
	}
}
