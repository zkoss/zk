/* SerializableEventListener.java

	Purpose:
		
	Description:
		
	History:
		Tue Nov 23 12:52:17 TST 2010, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zk.ui.event;

/**
 * A serializable event listener that will be notified when an event occurs,
 * if it is registered to {@link org.zkoss.zk.ui.Component#addEventListener}.
 *
 * <p>It is the same as {@link EventListener} except it also extends
 * java.io.Serializable. It is useful for instantiating a serializable instance of
 * an anonymous class (so it could work in a clustering environment). For example,
 * <pre><code>
 *comp.addEventListener("onSomething",
 *  new SerializableEventListener() {
 *    public void onEvent(Event event) {
 *      //...
 *    }
 *  });</code></pre>
 *
 * @author tomyeh
 * @since 5.0.6
 */
public interface SerializableEventListener extends EventListener, java.io.Serializable {
}
