/* ZephyrEventListenerMap.java

	Purpose:
		
	Description:
		
	History:
		12:20 PM 2021/10/7, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.ui.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.zkoss.util.ArraysX;
import org.zkoss.zephyr.action.data.ActionData;
import org.zkoss.zephyr.ui.UiAgent;
import org.zkoss.zephyr.ui.UiAgentCtrl;
import org.zkoss.zephyr.util.ActionHandler;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.StubEvent;
import org.zkoss.zk.ui.ext.Scope;
import org.zkoss.zk.ui.sys.EventListenerMap;
import org.zkoss.zk.ui.sys.EventListenerMapCtrl;

/**
 * A wrapper of {@link EventListenerMap} in zephyr.
 * @author jumperchen
 */
public class ZephyrEventListenerMap implements EventListenerMap,
		EventListenerMapCtrl, Serializable {
	private EventListenerMap delegator;

	private Map<String, List<ActionHandler>> _actions;

	public ZephyrEventListenerMap(EventListenerMap delegator) {
		this.delegator = delegator;
	}

	private void initMap() {
		if (_actions == null) {
			_actions = new HashMap<>(4);
		}
	}
	public void addAction(String eventName, ActionHandler actionHandler) {
		initMap();
		List<ActionHandler> methods = _actions.computeIfAbsent(eventName,
				(ignore) -> new ArrayList<ActionHandler>(4));
		methods.add(actionHandler);
	}

	public void service(Event event, Scope scope, Component component, String s)
			throws Exception {
		if (delegator != null) {
			delegator.service(event, scope, component, s);
		}
		if (_actions != null && !_actions.isEmpty()) {
			StubEvent stubEvent = (StubEvent) event;
			List<ActionHandler> actionHandlers = _actions.get(stubEvent.getCommand());
			if (actionHandlers != null) {
				Map<String, Object> requestData = stubEvent.getRequestData();
				String mtd = (String) requestData.get(ActionData.METHOD);

				// find a specific method if any.
				actionHandlers = mtd != null ? actionHandlers.stream()
						.filter(actionHandler -> actionHandler.method()
								.getName().equals(mtd))
						.collect(Collectors.toList()) : actionHandlers;
				for (ActionHandler handler : actionHandlers) {
					Object[] args = null;
					try {
						Class<?>[] parameterTypes = handler.method()
								.getParameterTypes();
						int diff = parameterTypes.length - handler.getParameterCount();
						args = ActionParameterResolver.resolve(stubEvent.getUuid(), requestData,
								diff > 0 ? ArraysX.shrink(parameterTypes, diff,
										parameterTypes.length) : parameterTypes);
						handler.call(args);
					} catch (Throwable e) {
						throw new UiException(e);
					} finally {
						if (args != null && args.length > 0) {
							for (Object arg : args) {
								if (arg != null && UiAgent.class.isAssignableFrom(
										arg.getClass())) {
									UiAgentCtrl.deactivate((UiAgent) arg);
								}
							}
						}
					}
				}
			}
		}
	}

	public Set<String> getEventNames() {
		if (delegator instanceof EventListenerMapCtrl) {
			return ((EventListenerMapCtrl) delegator).getEventNames();
		}
		return Collections.emptySet();
	}
}
