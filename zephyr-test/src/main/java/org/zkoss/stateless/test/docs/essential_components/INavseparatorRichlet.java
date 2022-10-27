/* INavseparatorRichlet.java

	Purpose:

	Description:

	History:
		Wed Apr 06 16:03:25 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.test.docs.essential_components;

import java.util.Arrays;
import java.util.List;

import org.zkoss.stateless.annotation.RichletMapping;
import org.zkoss.stateless.ui.StatelessRichlet;
import org.zkoss.stateless.sul.IComponent;
import org.zkoss.statelessex.sul.INavbar;
import org.zkoss.statelessex.sul.INavitem;
import org.zkoss.statelessex.sul.INavseparator;

/**
 * A set of example for {@link org.zkoss.statelessex.sul.INavseparator} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Essential_Components/Navseparator">INavseparator</a>,
 * if any.
 *
 * @author katherine
 * @see org.zkoss.statelessex.sul.INavseparator
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