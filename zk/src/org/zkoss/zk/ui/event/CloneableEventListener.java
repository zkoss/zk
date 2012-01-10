/* CloneableEventListener.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Jan 10, 2012 2:53:11 PM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2012 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zk.ui.event;

import org.zkoss.zk.ui.util.ComponentCloneListener;

/** A cloneable event listener that will be notified when an event occurs,
 * if it is registered to {@link org.zkoss.zk.ui.Component#addEventListener}.
 *
 * <p>It is the same as {@link EventListener} except it also extends
 * {@line ComponentCloneListener}. It is useful for instantiating a cloneable instance of
 * an anonymous class (so it could work in a cloned environment). For example,
 * <pre><code>
 *comp.addEventListener("onSomething",
 *  new CloneableEventListener() {
 *    public void onEvent(Event event) {
 *      //...
 *    }
 *  });</code></pre>
 *
 * @author jumperchen
 * @since 6.0.0
 */
public interface CloneableEventListener<T extends Event> extends EventListener<T>,
		ComponentCloneListener {
}
