/** ZKBinderPhaseListeners.java.

	Purpose:
		
	Description:
		
	History:
		4:33:43 PM Feb 3, 2015, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.init;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.bind.PhaseListener;
import org.zkoss.zk.ui.util.AggregationListener;

/**
 * An aggregating phase listeners
 * @author jumperchen
 * @since 8.0.0
 * @see PhaseListener
 */
public class ZKBinderPhaseListeners implements AggregationListener {
	private static final Logger _log = LoggerFactory.getLogger(ZKBinderPhaseListeners.class);
	private static List<PhaseListener> _listeners = new LinkedList<PhaseListener>();
	
	public boolean isHandled(Class<?> klass) {
		if (PhaseListener.class.isAssignableFrom(klass)) {
			try {
				_listeners.add((PhaseListener) klass.newInstance());
			} catch (Exception e) {
				_log.error("Error when initial phase listener:"+klass , e);
			}
			return true;
		}
		return false;
	}
	
	/**
	 * Returns all of the system phase listeners
	 */
	public static List<PhaseListener> getSystemPhaseListeners() {
		return _listeners;
	}
}
