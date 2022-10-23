/* IColumnchildrenRichlet.java

	Purpose:

	Description:

	History:
		Mon Apr 18 12:31:24 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.test.docs.layouts;

import org.zkoss.zephyr.annotation.RichletMapping;
import org.zkoss.zephyr.ui.StatelessRichlet;
import org.zkoss.zephyr.zpr.ILabel;
import org.zkoss.zephyrex.zpr.IColumnchildren;
import org.zkoss.zephyrex.zpr.IColumnlayout;

/**
 * A set of example for {@link org.zkoss.zephyrex.zpr.IColumnchildren} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Layouts/Columnlayout/Columnchildren">IColumnChildren</a>,
 * if any.
 *
 * @author katherine
 * @see org.zkoss.zephyrex.zpr.IColumnchildren
 */
@RichletMapping("/layouts/iColumnChildren")
public class IColumnchildrenRichlet implements StatelessRichlet {
	@RichletMapping("/columnlayout")
	public IColumnlayout columnlayout() {
		return IColumnlayout.of(
				IColumnchildren.ofId("children").withChildren(ILabel.of("child")).withWidth("20%"),
				IColumnchildren.ofId("children2").withChildren(ILabel.of("child")).withWidth("80%")
		);
	}
}