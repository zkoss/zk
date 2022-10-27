/* ITabboxRichlet.java

	Purpose:

	Description:

	History:
		2:15 PM 2022/3/3, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.test.docs.containers;

import org.zkoss.stateless.annotation.RichletMapping;
import org.zkoss.stateless.ui.StatelessRichlet;
import org.zkoss.stateless.sul.ILabel;
import org.zkoss.stateless.sul.ITab;
import org.zkoss.stateless.sul.ITabbox;
import org.zkoss.stateless.sul.ITabpanel;
import org.zkoss.stateless.sul.ITabpanels;
import org.zkoss.stateless.sul.ITabs;

/**
 * A set of example for {@link ITabbox} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Containers/Tabbox">ITabbox</a>,
 * if any.
 *
 * @author jumperchen
 * @see ITabbox
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
