/* BindUtils.java

	Purpose:
		
	Description:
		
	History:
		2012/1/31 Created by Dennis Chen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind;

import java.util.Map;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.proxy.ViewModelProxyObject;
import org.zkoss.bind.sys.BinderCtrl;
import org.zkoss.lang.Strings;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;

/**
 * A utility to help developer using zk bind
 * @author dennis
 *
 */
public class BindUtils {

	/**
	 * Post a global command to corresponding event queue
	 * @param queueName the queue name, null for default queue name
	 * @param queueScope the queue scope, null for default queue scope (i.e. {@link EventQueues#DESKTOP})
	 * @param cmdName the global command name
	 * @param args arguments, could get the data in command method by {@link BindingParam}
	 */
	public static void postGlobalCommand(String queueName, String queueScope, String cmdName,
			Map<String, Object> args) {
		final EventQueue<Event> que = EventQueues.lookup(queueName == null ? BinderCtrl.DEFAULT_QUEUE_NAME : queueName,
				queueScope == null ? BinderCtrl.DEFAULT_QUEUE_SCOPE : queueScope, false);
		if (Strings.isEmpty(cmdName))
			throw new IllegalArgumentException("cmdName is empty");
		if (que != null) {
			que.publish(new GlobalCommandEvent(null, cmdName, args));
		}
	}

	/**
	 * Post a notify change to corresponding event queue to notify a bean's property changing
	 * @param queueName the queue name, null for default queue name
	 * @param queueScope the queue scope, null for default queue scope (i.e. {@link EventQueues#DESKTOP})
	 * @param bean the bean instance
	 * @param property the property name of bean
	 */
	public static void postNotifyChange(String queueName, String queueScope, Object bean, String property) {
		final EventQueue<Event> que = EventQueues.lookup(queueName == null ? BinderCtrl.DEFAULT_QUEUE_NAME : queueName,
				queueScope == null ? BinderCtrl.DEFAULT_QUEUE_SCOPE : queueScope, false);
		if (Strings.isEmpty(property))
			throw new IllegalArgumentException("property is empty");
		if (que != null) {
			que.publish(new PropertyChangeEvent(null, bean, property));
		}
	}
	
	/**
	 * Post a notify change to corresponding event queue to notify a bean's properties changing
	 * Accept multiple properties for convenience
	 * @param queueName the queue name, null for default queue name
	 * @param queueScope the queue scope, null for default queue scope (i.e. {@link EventQueues#DESKTOP})
	 * @param bean the bean instance
	 * @param properties the properties name of bean
	 * @see #postNotifyChange(String, String, Object, String)
	 * @since 8.5.2
	 */
	public static void postNotifyChange(String queueName, String queueScope, Object bean, String... properties) {
		for (String property : properties) {
			postNotifyChange(queueName, queueScope, bean, property);
		}
	}

	public static Class<?> getViewModelClass(Object viewModel) {
		if (viewModel instanceof ViewModelProxyObject)
			return ((ViewModelProxyObject) viewModel).getOriginClass();
		return viewModel.getClass();
	}
}
