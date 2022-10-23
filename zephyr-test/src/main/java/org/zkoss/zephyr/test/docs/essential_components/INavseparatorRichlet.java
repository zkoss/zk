/* INavseparatorRichlet.java

	Purpose:

	Description:

	History:
		Wed Apr 06 16:03:25 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.test.docs.essential_components;

import java.util.Arrays;
import java.util.List;

import org.zkoss.zephyr.annotation.RichletMapping;
import org.zkoss.zephyr.ui.StatelessRichlet;
import org.zkoss.zephyr.zpr.IComponent;
import org.zkoss.zephyrex.zpr.INavbar;
import org.zkoss.zephyrex.zpr.INavitem;
import org.zkoss.zephyrex.zpr.INavseparator;

/**
 * A set of example for {@link org.zkoss.zephyrex.zpr.INavseparator} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Essential_Components/Navseparator">INavseparator</a>,
 * if any.
 *
 * @author katherine
 * @see org.zkoss.zephyrex.zpr.INavseparator
 */
@RichletMapping("/essential_components/iNavseparator")
public class INavseparatorRichlet implements StatelessRichlet {
	@RichletMapping("")
	public List<IComponent> navseparator() {
		return Arrays.asList(
				INavbar.of(
						INavitem.of("nav 1"),
						INavseparator.DEFAULT,
						INavitem.of("nav 2")
				)
		);
	}
}