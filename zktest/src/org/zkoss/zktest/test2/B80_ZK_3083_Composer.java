/* B80_ZK_3083_Composer.java

	Purpose:
		
	Description:
		
	History:
		Wed Feb 17 16:12:46 CST 2016, Created by jameschu

Copyright (C) 2016 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.VisibilityChangeEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Window;

/**
 * 
 * @author jameschu
 */
public class B80_ZK_3083_Composer extends SelectorComposer<Window> {

	public void doAfterCompose(Window comp) throws Exception {
		super.doAfterCompose(comp);
		comp.addEventListener(Events.ON_VISIBILITY_CHANGE, new VisibilityEventListener());
	}

	private class VisibilityEventListener implements EventListener<VisibilityChangeEvent> {
		public void onEvent(VisibilityChangeEvent evt) throws Exception {
			Clients.log("Visbility event triggered");
		}
	}
}
