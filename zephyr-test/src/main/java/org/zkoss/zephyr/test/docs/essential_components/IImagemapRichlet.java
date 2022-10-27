/* IImagemapRichlet.java

		Purpose:
		
		Description:
		
		History:
				Thu Jan 13 12:06:19 CST 2022, Created by leon

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.test.docs.essential_components;

import org.zkoss.stateless.action.data.MouseData;
import org.zkoss.stateless.annotation.Action;
import org.zkoss.stateless.annotation.RichletMapping;
import org.zkoss.stateless.ui.StatelessRichlet;
import org.zkoss.stateless.zpr.IArea;
import org.zkoss.stateless.zpr.IComponent;
import org.zkoss.stateless.zpr.IImagemap;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;

/**
 * A set of example for {@link IImagemap} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Essential_Components/Imagemap">IImagemap</a>,
 * if any.
 * @author leon
 * @see IImagemap
 */
@RichletMapping("/essential_components/iimagemap")
public class IImagemapRichlet implements StatelessRichlet {

	@RichletMapping("/example")
	public IComponent example() {
		return IImagemap.of(
				IArea.ofId("left").withCoords("0, 0, 45, 80"),
				IArea.ofId("right").withCoords("46, 0, 90, 80")
		).withSrc("/zephyr/ZK-Logo.gif").withAction(this::doClick);
	}

	@Action(type = Events.ON_CLICK)
	public void doClick(MouseData mouseData) {
		Clients.log(mouseData.getArea());
	}
}
