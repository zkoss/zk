/* DetachConverter.java

	Purpose:
		
	Description:
		
	History:
		2012/4/13 Created by dennis

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.converter;

import java.io.Serializable;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Converter;
import org.zkoss.lang.Classes;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;


/**
 * A special converter that help you detach a component.<br/>
 * For example <br/>
 * <pre>
 * {@code
 * <window title="window1" 
 *    whatever="@load(true,after='detach') @converter('org.zkoss.bind.converter.DetachConverter')">
 *    <button label="detach" onClick="@command('detach')"/>
 * </window>
 * }
 * </pre>
 * At here, <i>whatever</i> is a non-existed attribute of the component
 * 
 * @author dennis
 * @since 6.0.1
 */
public class DetachConverter implements Converter,Serializable {
	
	private static final long serialVersionUID = 1463169907348730644L;

	@Override
	public Object coerceToUi(Object val, Component component, BindContext ctx) {
		Boolean b = (Boolean)Classes.coerce(Boolean.class, val);
		if(b!=null && b.booleanValue()){
			Event evt = new Event("onPostDetach",component);
			component.addEventListener(evt.getName(), _listener);
			Events.postEvent(evt);
		}
		return IGNORED_VALUE;
	}
	static private PostDetachListener _listener = new PostDetachListener();
	static class PostDetachListener implements EventListener<Event>,Serializable{
		private static final long serialVersionUID = 1L;
		@Override
		public void onEvent(Event event) throws Exception {
			Component comp = event.getTarget();
			comp.removeEventListener(event.getName(), this);
			comp.detach();
		}
	}

	@Override
	public Object coerceToBean(Object val, Component component, BindContext ctx) {
		return val;
	}

}
