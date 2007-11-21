/* GenericEventListener.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Nov 21, 2007 6:10:38 PM , Created by robbiecheng
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.event;

import java.lang.reflect.Method;

import org.zkoss.zk.ui.sys.ComponentsCtrl;

/**
 * A generic event listener which will invoke corresponding event handlers automatically.
 * @author robbiecheng
 * @since 3.0.1
 *
 */
public class GenericEventListener implements EventListener {

	/* (non-Javadoc)
	 * @see org.zkoss.zk.ui.event.EventListener#onEvent(org.zkoss.zk.ui.event.Event)
	 */	
	public void onEvent(Event evt) throws Exception {		
		final Method mtd =	ComponentsCtrl.getEventMethod(this.getClass(), evt.getName());
		if (mtd != null) {
//			if (log.finerable()) log.finer("Method for event="+evtnm+" comp="+_comp+" method="+mtd);
			if (mtd.getParameterTypes().length == 0)
				mtd.invoke(this, null);
			else
				mtd.invoke(this, new Object[] {evt.getData()});
			if (!evt.isPropagatable())
				return; //done
		}

	}
}
