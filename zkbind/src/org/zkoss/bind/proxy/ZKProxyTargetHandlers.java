/** ZKProxyTargetHandlers.java.

	Purpose:

	Description:

	History:
 Tue Apr 19 16:12:31 CST 2016, Created by jameschu

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.proxy;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.zkoss.bind.init.ZKBinderPhaseListeners;
import org.zkoss.zk.ui.util.AggregationListener;

/**
 * An aggregating proxy target handlers
 * @author jameschu
 * @since 8.0.2
 * @see ProxyTargetHandler
 */
public class ZKProxyTargetHandlers implements AggregationListener {
	private static final Logger _log = LoggerFactory.getLogger(ZKBinderPhaseListeners.class);
	private static Map<String, ProxyTargetHandler> _handlers = new LinkedHashMap<String, ProxyTargetHandler>();

	public boolean isHandled(Class<?> klass) {
		if (ProxyTargetHandler.class.isAssignableFrom(klass)) {
			try {
				synchronized (_handlers) {
					if (!_handlers.containsKey(klass.getName())) {
						_handlers.put(klass.getName(), (ProxyTargetHandler) klass.newInstance());
					}
				}
			} catch (Exception e) {
				_log.error("Error when initial proxy target handler:" + klass, e);
			}
			return true;
		}
		return false;
	}

	/**
	 * Returns all of the system proxy target handlers
	 */
	public static List<ProxyTargetHandler> getSystemProxyTargetHandlers() {
		Collection<ProxyTargetHandler> values;
		synchronized (_handlers) {
			values = _handlers.values();
		}
		return new LinkedList<ProxyTargetHandler>(values);
	}
}
