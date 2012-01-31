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
import org.zkoss.bind.impl.GlobalCommandEvent;
import org.zkoss.bind.impl.PropertyChangeEvent;
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
	 * Default queue name of a binder to share the bean notification and global commands
	 */
	public static final String DEFAULT_QUEUE_NAME = "$ZKBIND_DEFQUE$"; //the associated event queue name
	
	/**
	 * Default queue scope of a binder to share the bean notification and global commands
	 * @see EventQueues
	 */
	public static final String DEFAULT_QUEUE_SCOPE = EventQueues.DESKTOP; //the associated event queue name
	
	
	/**
	 * Post a global command to responding event queue
	 * @param queueName the queue name, use {@link #DEFAULT_QUEUE_NAME} if null
	 * @param queueScope the queue scope, use {@link #DEFAULT_QUEUE_SCOPE} if null
	 * @param cmdName the global command name
	 * @param args arguments, could get the data in command method by {@link BindingParam}
	 */
	public static void postGlobalCommand(String queueName, String queueScope, String cmdName, Map<String,Object> args){
		final EventQueue<Event> que = EventQueues.lookup(queueName==null?DEFAULT_QUEUE_NAME:queueName, queueScope==null?DEFAULT_QUEUE_SCOPE:queueScope, false);
		if (que != null) {
			que.publish(new GlobalCommandEvent(null, cmdName, args));
		}
	}
	
	/**
	 * Post a notify change to responding event queue to notify a bean's property changing
	 * @param queueName the queue name, use {@link #DEFAULT_QUEUE_NAME} if null
	 * @param queueScope the queue scope, use {@link #DEFAULT_QUEUE_SCOPE} if null
	 * @param bean the bean instance
	 * @param property the property name of bean
	 */
	public static void postNotifyChange(String queueName, String queueScope, Object bean, String property){
		final EventQueue<Event> que = EventQueues.lookup(queueName==null?DEFAULT_QUEUE_NAME:queueName, queueScope==null?DEFAULT_QUEUE_SCOPE:queueScope, false);
		if (que != null) {
			que.publish(new PropertyChangeEvent(null, bean, property));
		}
	}
}
