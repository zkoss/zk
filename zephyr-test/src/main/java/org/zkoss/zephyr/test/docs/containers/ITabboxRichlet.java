/* ITabboxRichlet.java

	Purpose:

	Description:

	History:
		2:15 PM 2022/3/3, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.test.docs.containers;

import org.zkoss.zephyr.annotation.RichletMapping;
import org.zkoss.zephyr.ui.StatelessRichlet;
import org.zkoss.zephyr.zpr.ILabel;
import org.zkoss.zephyr.zpr.ITab;
import org.zkoss.zephyr.zpr.ITabbox;
import org.zkoss.zephyr.zpr.ITabpanel;
import org.zkoss.zephyr.zpr.ITabpanels;
import org.zkoss.zephyr.zpr.ITabs;

/**
 * A set of example for {@link org.zkoss.zephyr.zpr.ITabbox} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Containers/Tabbox">ITabbox</a>,
 * if any.
 *
 * @author jumperchen
 * @see org.zkoss.zephyr.zpr.ITabbox
 */
@RichletMapping("/containers/itabbox")
public class ITabboxRichlet implements StatelessRichlet {

	@RichletMapping("/mold/default")
	public ITabbox defaultMold() {
		return ITabbox.of(ITabs.of(ITab.of("Tab1"), ITab.of("Tab2")),
				ITabpanels.of(ITabpanel.of(ILabel.of("This is panel 1")),
						ITabpanel.of(ILabel.of("This is panel 2"))));
	}

	@RichletMapping("/mold/accordion")
	public ITabbox accordionMold() {
		return defaultMold().withMold("accordion");
	}

	@RichletMapping("/orient/top")
	public ITabbox topOrient() {
		return defaultMold();
	}

	@RichletMapping("/orient/left")
	public ITabbox leftOrient() {
		return ITabbox.of(ITabs.of(ITab.of("Tab1"), ITab.of("Tab2")).withWidth("100px"),
				ITabpanels.of(ITabpanel.of(ILabel.of("This is panel 1")),
						ITabpanel.of(ILabel.of("This is panel 2")))).withOrient(
				ITabbox.Orient.LEFT);
	}

	@RichletMapping("/orient/right")
	public ITabbox rightOrient() {
		return leftOrient().withOrient(ITabbox.Orient.RIGHT);
	}

	@RichletMapping("/orient/bottom")
	public ITabbox bottomOrient() {
		return topOrient().withOrient(ITabbox.Orient.BOTTOM);
	}

}
