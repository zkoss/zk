/* AfterCompose.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu May 25 11:15:15     2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.ext;

/**
 * Implemented by a component if it needs the {@link #afterCompose} callback.
 * {@link #afterCompose} is called after ZK loader creates this component,
 * all of its children, and assigns all properties defined in the ZUML page.
 * It is so-called "composing".
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
 * <p>If it is created manually, it is caller's job to invoke afterCompose.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public interface AfterCompose {
	/** Invokes after ZK loader applies all properties.
	 * Its meaning depends on the implementation class.
	 */
	public void afterCompose();
}
