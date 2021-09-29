/* EventListenerMapCtrl.java

	Purpose:
		
	Description:
		
	History:
		2:14 PM 2021/9/28, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zk.ui.sys;

import java.util.Set;

/**
 * An addition interface to {@link EventListenerMap}
 * that is used for implementation or tools.
 * @author jumperchen
 * @since 10.0.0
 */
public interface EventListenerMapCtrl {
	/**
	 * Returns all event names of this map.
	 * @return
	 */
	public Set<String> getEventNames();
}
