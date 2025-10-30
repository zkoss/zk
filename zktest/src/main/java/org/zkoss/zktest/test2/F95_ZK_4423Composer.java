/* F95_ZK_4423Composer.java

		Purpose:
		
		Description:
		
		History:
				Thu Nov 05 16:09:54 CST 2020, Created by leon

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zkmax.ui.event.PortalDropEvent;
import org.zkoss.zkmax.ui.event.PortalMoveEvent;
import org.zkoss.zkmax.zul.Portalchildren;
import org.zkoss.zkmax.zul.Portallayout;
import org.zkoss.zul.Panel;

public class F95_ZK_4423Composer extends SelectorComposer<Component> {
	@Wire
	Portallayout pl;

	@Wire
	Portallayout pl2;

	@Wire
	Portallayout pl3;

	@Listen("onPortalDrop = #pl")
	public void onPortalDrop(PortalDropEvent event) {
		Clients.log(event.getName());
		if (Math.abs(event.getDroppedColumnIndex() - event.getDraggedColumnIndex()) <= 1) {
			final Portalchildren to = event.getTo();
			int droppedIndex = event.getDroppedIndex();
			final Panel dragged = event.getDragged();

			if (dragged.getParent() == to // //same parent
				&& droppedIndex >= to.getChildren().indexOf(dragged))
				droppedIndex++;

			to.insertBefore(dragged,
				droppedIndex < to.getChildren().size() ? to.getChildren().get(droppedIndex) : null);
			Clients.log("moved!");
		} else {
			Clients.log("cross 2 column!");
		}
		event.preventDefault();
	}

	@Listen("onPortalMove = #pl")
	public void onPortalMove(PortalMoveEvent event) {
		Clients.log(event.getName());
	}

	@Listen("onPortalDrop = #pl2")
	public void onPortalDrop2(PortalDropEvent event) {
		Clients.log(event.getName());
	}

	@Listen("onPortalMove = #pl2")
	public void onPortalMove2(PortalMoveEvent event) {
		Clients.log(event.getName());
	}

	@Listen("onPortalDrop = #pl3")
	public void onPortalDrop3(PortalDropEvent event) {
		Clients.log(event.getName());
		event.getDragged().setVisible(false);
		event.preventDefault();
	}

	@Listen("onPortalMove = #pl3")
	public void onPortalMove3(PortalMoveEvent event) {
		Clients.log(event.getName());
	}
}
