/* IAnchornavRichlet.java

	Purpose:

	Description:

	History:
		12:19 PM 2022/3/21, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.test.docs.essential_components;

import org.zkoss.stateless.annotation.RichletMapping;
import org.zkoss.stateless.ui.StatelessRichlet;
import org.zkoss.stateless.sul.IA;
import org.zkoss.stateless.sul.IComponent;
import org.zkoss.stateless.sul.IDiv;
import org.zkoss.stateless.sul.IHlayout;
import org.zkoss.stateless.sul.ILabel;
import org.zkoss.stateless.sul.IListbox;
import org.zkoss.stateless.sul.IListcell;
import org.zkoss.stateless.sul.IListitem;
import org.zkoss.stateless.sul.IWindow;
import org.zkoss.statelessex.sul.IAnchornav;

/**
 * A set of example for {@link org.zkoss.statelessex.sul.IAnchornav} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Essential_Components/Anchornav">IAnchornav</a>,
 * if any.
 * @author jumperchen
 * @see org.zkoss.statelessex.sul.IAnchornav
 */
@RichletMapping("/essential_components/ianchornav")
public class IAnchornavRichlet implements StatelessRichlet {
	@RichletMapping("/example")
	public IComponent example() {
		return IHlayout.of(IAnchornav.of(IListbox.of(
						IListitem.of(IListcell.of(IA.of("First Window").withClientAttribute("data-anchornav-target", "$win1"))),
						IListitem.of(IListcell.of(IA.of("Second Window").withClientAttribute("data-anchornav-target", "$win2")))
						)
				).withName("a1").withWidth("250px"), IDiv.of(
				IWindow.ofId("win1").withHeight("700px").withBorder(
						IWindow.Border.NORMAL).withTitle("1. First Window").withChildren(
						ILabel.of("Hello world.")),
				IWindow.ofId("win2").withHeight("700px").withBorder(
						IWindow.Border.NORMAL).withTitle("2. Second Window").withChildren(
						ILabel.of("Welcome to ZK world."))
				).withWidth("750px"));
	}
}
