/* IMenupopupRichlet.java

	Purpose:

	Description:

	History:
		Tue Apr 12 11:43:37 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.test.docs.essential_components;

import org.zkoss.zephyr.annotation.RichletMapping;
import org.zkoss.zephyr.ui.StatelessRichlet;
import org.zkoss.zephyr.zpr.IComponent;
import org.zkoss.zephyr.zpr.IMenu;
import org.zkoss.zephyr.zpr.IMenubar;
import org.zkoss.zephyr.zpr.IMenuitem;
import org.zkoss.zephyr.zpr.IMenupopup;

/**
 * A set of example for {@link org.zkoss.zephyr.zpr.IMenupopup} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Essential_Components/Menu/Menupopup">IMenupopup</a>,
 * if any.
 *
 * @author katherine
 * @see org.zkoss.zephyr.zpr.IMenupopup
 */
@RichletMapping("/essential_components/iMenupopup")
public class IMenupopupRichlet implements StatelessRichlet {
	@RichletMapping("/menupopup")
	public IComponent menupopup() {
		return IMenubar.of(
				IMenu.DEFAULT.withChild(
						IMenupopup.ofId("popup").withChildren(
								IMenuitem.of("item 1"),
								IMenuitem.of("item 2")
						)
				)
		);
	}
}