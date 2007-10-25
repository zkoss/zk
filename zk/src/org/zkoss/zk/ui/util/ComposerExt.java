/* ComposerExt.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Oct 25 19:37:27     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.util;

import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.metainfo.ComponentInfo;

/**
 * 
 * @author tomyeh
 * @since 3.0.0
 */
public interface ComposerExt {
	/** Invokes before composing a component.
	 *
	 * @param page the page for composing this component.
	 * @param parent the parent component, or null if it is the root component.
	 * @param compInfo the component info used to instantiate the component.
	 * @return the component info used to instantiate the component.
	 * In most case, it shall return compInfo.
	 * If null is returned, the component won't be instantiated.
	 * In other words, it is ignored.
	 */
	public ComponentInfo doBeforeCompose(Page page, Component parent,
	ComponentInfo compInfo);
	/** Invokes after the component is instantiated and initialized, but
	 * before composing any child.
	 * @param comp the component being composed
	 */
	public void doBeforeComposeChildren(Component comp) throws Exception;
	/** Called when an exception occurs when composing the component.
	 *
	 * <p>This is only a notification for the happening of an exception.
	 * You DO NOT re-throw exception here.
	 * The exception is always re-throwed after calling this method
	 * @param ex the exception being thrown
	 */
	public void doCatch(Throwable ex);
	/** Do the cleanup after the component has been composed.
	 * It is always called no matter whether {@link #doCatch},
	 * {@link #doBeforeComposeChildren} or
	 * {@link Composer#doAfterCompose} is called.
	 *
	 * <p>Note: it is called before the onCreate event is handled.
	 */
	public void doFinally();
}
