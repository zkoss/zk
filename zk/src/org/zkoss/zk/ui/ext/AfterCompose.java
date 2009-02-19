/* AfterCompose.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu May 25 11:15:15     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.ext;

/**
 * Implemented by a component if it wants to know when ZK loader created it.
 * If this interface is implemented,
 * {@link #afterCompose} is called, after ZK loader creates this component,
 * all of its children, and assigns all properties defined in the ZUML page.
 * It is so-called "compose".
 *
 * <p>It is similar to listen the onCreate event since it is called after
 * all children are created. However, unlike onCreate, it is called in
 * the Component Creation phase (rather than in an event listener).
 * In other words, it is called before processing an event including onCreate
 * events. Since no event processing thread is forked, the performance is
 * a bit better.
 *
 * <p>A typical example is that macro components use this callback to
 * create its children based on the macro URI.
 *
 * <p>If it is created manually, it is caller's job to invoke {@link #afterCompose}.
 *
 * <p>{@link AfterCompose} has to be implemented as part of
 * a component. On the other hand, {@link org.zkoss.zk.ui.util.Composer} is
 * an controller used to intiailize a component.
 *
 * @author tomyeh
 * @see org.zkoss.zk.ui.util.Composer
 */
public interface AfterCompose {
	/** Invokes after ZK loader creates this component,
	 * initializes it and composes all its children, if any.
	 */
	public void afterCompose();
}
