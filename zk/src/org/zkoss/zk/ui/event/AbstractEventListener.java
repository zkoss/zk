/* AbstractEventListener.java

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
import org.zkoss.zk.ui.Component;

import org.zkoss.zk.ui.sys.ComponentsCtrl;

/**
 * <p>An abstract event listener that you can extend and write intuitive onXxx event 
 * handler methods; this class dispatch event to the implemented onXxx event handler methods
 * automatically. It also provides a convenent method {@link #bindComponent} that you 
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
 * public class MyEventListener extends AbstractEventListener {
 *    public void onOK() {
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
abstract public class AbstractEventListener implements EventListener {

	/* (non-Javadoc)
	 * @see org.zkoss.zk.ui.event.EventListener#onEvent(org.zkoss.zk.ui.event.Event)
	 */	
	public void onEvent(Event evt) throws Exception {		
		final Method mtd =	ComponentsCtrl.getEventMethod(this.getClass(), evt.getName());
		if (mtd != null) {
			if (mtd.getParameterTypes().length == 0)
				mtd.invoke(this, null);
			else
				mtd.invoke(this, new Object[] {evt.getData()});
		}
	}
	
	/**
	 * A convenient method that help you register this event listener to the specified
	 * target component.
	 * @param comp the target component the event is posted (or sent) to.
	 */
	public void bindComponent(Component comp) {
		final Method [] metds = getClass().getMethods();
		for(int i=0; i < metds.length; i ++){
			final String evtnm = metds[i].getName();
			if (evtnm.startsWith("on"))
				comp.addEventListener(evtnm, this);
		}		
	}
}
