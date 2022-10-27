/* IAbsolutelayoutRichlet.java

	Purpose:

	Description:

	History:
		3:54 PM 2022/2/14, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.test.docs.layouts;

import org.zkoss.stateless.annotation.RichletMapping;
import org.zkoss.stateless.ui.StatelessRichlet;
import org.zkoss.stateless.zpr.IAbsolutechildren;
import org.zkoss.stateless.zpr.IAbsolutelayout;
import org.zkoss.stateless.zpr.IComponent;
import org.zkoss.stateless.zpr.ILabel;
import org.zkoss.stateless.zpr.IWindow;

/**
 * A set of example for {@link IAbsolutelayout} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Layouts/Absolutelayout">IAbsolutelayout</a>,
 * if any.
 * @author jumperchen
 * @see IAbsolutelayout
 */
@RichletMapping("/layouts/iabsolutelayout")
public class IAbsolutelayoutRichlet implements StatelessRichlet {

	@RichletMapping("/example")
	public IComponent example() {
		return IAbsolutelayout.of(IAbsolutechildren.of(60, 100,
						IWindow.ofTitle("X=60, Y=100").withBorder("normal")
								.withChildren(ILabel.of("Window 1"))),
				IAbsolutechildren.of(160, 200,
						IWindow.ofTitle("X=160, Y=200").withBorder("normal")
								.withChildren(ILabel.of("Window 2"))),
				IAbsolutechildren.of(260, 300,
						IWindow.ofTitle("X=260, Y=300").withBorder("normal")
								.withChildren(ILabel.of("Window 3"))));
	}
}
