/* IBorderlayoutRichlet.java

	Purpose:

	Description:

	History:
		Tue Apr 12 10:34:37 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.test.docs.layouts;

import org.zkoss.zephyr.annotation.RichletMapping;
import org.zkoss.zephyr.ui.StatelessRichlet;
import org.zkoss.zephyr.zpr.IBorderlayout;
import org.zkoss.zephyr.zpr.ICenter;
import org.zkoss.zephyr.zpr.IComponent;
import org.zkoss.zephyr.zpr.IEast;
import org.zkoss.zephyr.zpr.INorth;
import org.zkoss.zephyr.zpr.ISouth;
import org.zkoss.zephyr.zpr.IWest;

/**
 * A set of example for {@link org.zkoss.zephyr.zpr.IBorderlayout} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Layouts/Borderlayout">IBorderlayout</a>,
 * if any.
 *
 * @author katherine
 * @see org.zkoss.zephyr.zpr.IBorderlayout
 */
@RichletMapping("/layouts/iBorderlayout")
public class IBorderlayoutRichlet implements StatelessRichlet {
	@RichletMapping("/children")
	public IComponent children() {
		return IBorderlayout.of(
					INorth.DEFAULT.withSize("10%").withTitle("North"),
					IWest.DEFAULT.withSize("25%").withTitle("West").withMaxsize(300)
							.withCollapsible(true).withSplittable(true),
					ICenter.DEFAULT.withTitle("Center"),
					IEast.DEFAULT.withSize("25%").withTitle("East"),
					ISouth.DEFAULT.withSize("100px").withTitle("South")
			).withWidth("800px");
	}
}