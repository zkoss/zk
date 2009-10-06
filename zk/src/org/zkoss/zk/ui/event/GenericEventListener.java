/* GenericEventListener.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Nov 21, 2007 6:10:38 PM , Created by robbiecheng
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.event;

import java.lang.reflect.Method;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.sys.ComponentsCtrl;

/**
 * <p>An abstract event listener that you can extend and write intuitive onXxx event 
 * handler methods; this class dispatch event to the implemented onXxx event handler methods
 * automatically. It also provides a convenient method {@link #bindComponent} that you 
 * can bind a target event component to this event listener easily.</p>
 * <p>Following is an example. Whenever onOK or onCancel is posted (or sent) to this event
 * listener, it dispatch the control to the corresponding defined onOK() and onCancel() methods
 * respectively. Note how the bindComponent() method bind this event listener to the main
 * window.</p>
 *<pre><code>
 * &lt;window id="main">
 *     ...
 * &lt;/window>
 * 
 * &lt;zscript>&lt;!-- both OK in zscript or a compiled Java class -->
 * public class MyEventListener extends GenericEventListener {
 *    public void onOK(Event evt) {
 *        //doOK!
 *        //...
 *    }
 *    public void onCancel() {
 *        //doCancel
 *        //...
 *    } 
 * }
 *
 * new MyEventListener().bindComponent(main);
 * &lt;/zscript>
 * </code></pre>
 * @author robbiecheng
 * @since 3.0.1
 *
 */
abstract public class GenericEventListener implements EventListener {

	/* Process the event by forwarding the invocation to
	 * the corresponding method called onXxx.
	 *
	 * <p>You rarely need to override this method.
	 * Rather, provide corresponding onXxx method to handle the event.
	 * 
	 * <p>Since 3.0.8, this method treats ForwardEvent specially. If the 
	 * event argument passed into this listener is a ForwardEvent and the
	 * defined onXxx method specifies a specific event class
	 * as its parameter rather than generic Event or ForwardEvent class, then this 
	 * method will unwrap the ForwardEvent automatically 
	 * (see {@link org.zkoss.zk.ui.event.ForwardEvent#getOrigin()})
	 * and pass the original forwarded event to the onXxx method.</p>
	 *
	 * @see org.zkoss.zk.ui.event.EventListener#onEvent(org.zkoss.zk.ui.event.Event)
	 */	
	public void onEvent(Event evt) throws Exception {		
		final Object controller = getController();
		final Method mtd =	ComponentsCtrl.getEventMethod(controller.getClass(), evt.getName());
		if (mtd != null) {
			if (mtd.getParameterTypes().length == 0)
				mtd.invoke(controller, null);
			else if (evt instanceof ForwardEvent) { //ForwardEvent
				final Class paramcls = (Class) mtd.getParameterTypes()[0];
				//paramcls is ForwardEvent || Event
				if (ForwardEvent.class.isAssignableFrom(paramcls)
				|| Event.class.equals(paramcls)) { 
					mtd.invoke(controller, new Object[] {evt});
				} else {
					do {
						evt = ((ForwardEvent)evt).getOrigin();
					} while(evt instanceof ForwardEvent);
					mtd.invoke(controller, new Object[] {evt});
				}
			} else
				mtd.invoke(controller, new Object[] {evt});
		}
	}
	
	/**
	 * A convenient method that help you register this event listener
	 * to the specified target component.
	 *
	 * <p>All public methods whose names start with "on" are considered
	 * as event handlers and the correponding event is listened.
	 * For example, if the derived class has a method named onOK,
	 * then the onOK event is listened and the onOK method is called
	 * when the event is received.
	 *
	 * @param comp the target component to register this event listener.
	 */
	public void bindComponent(Component comp) {
		final Method [] metds = getController().getClass().getMethods();
		for(int i=0; i < metds.length; i ++){
			final String evtnm = metds[i].getName();
			if (Events.isValid(evtnm))
				comp.addEventListener(evtnm, this);
		}		
	}

	/**
	 * A convenient method that help you remove this event listener from the
	 * specified target component. This is a counter method of the {@link #bindComponent(Component)}
	 * method.
	 *
	 * @param comp the target component to remove this event listener.
	 * @since 3.6.3
	 */
	public void unbindComponent(Component comp) {
		final Method [] metds = getController().getClass().getMethods();
		for(int i=0; i < metds.length; i ++){
			final String evtnm = metds[i].getName();
			if (Events.isValid(evtnm))
				comp.removeEventListener(evtnm, this);
		}		
	}

	/**
	 * Returns the controller that really implement the 
	 * onXxx methods (default to this). It is intended to be overrode;
	 * you shall not have to call this method directly.
	 * @since 3.0.8 
	 */
	protected Object getController() {
		return this;
	}
}
