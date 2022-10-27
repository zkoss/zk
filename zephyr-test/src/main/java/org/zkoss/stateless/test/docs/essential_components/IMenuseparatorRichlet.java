/* IMenuseparatorRichlet.java

	Purpose:

	Description:

	History:
		Tue Apr 12 11:43:08 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.test.docs.essential_components;

import java.util.Arrays;
import java.util.List;

import org.zkoss.stateless.annotation.RichletMapping;
import org.zkoss.stateless.ui.StatelessRichlet;
import org.zkoss.stateless.sul.IComponent;
import org.zkoss.stateless.sul.IMenu;
import org.zkoss.stateless.sul.IMenubar;
import org.zkoss.stateless.sul.IMenuitem;
import org.zkoss.stateless.sul.IMenupopup;
import org.zkoss.stateless.sul.IMenuseparator;

/**
 * A set of example for {@link IMenuseparator} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Essential_Components/Menu/Menuseparator">IMenuseparator</a>,
 * if any.
 *
 * @author katherine
 * @see IMenuseparator
 */
@RichletMapping("/essential_components/iMenuseparator")
public class IMenuseparatorRichlet implements StatelessRichlet {
	@RichletMapping("/menuseparator")
	public List<IComponent> menuseparator() {
		return Arrays.asList(
				IMenubar.of(
						IMenu.DEFAULT.withChild(
								IMenupopup.of(
										IMenuitem.of("item 1"),
										IMenuseparator.DEFAULT.withWidth("10px"),
										IMenuitem.of("item 2")
								)
						)
				)
		);
	}
}