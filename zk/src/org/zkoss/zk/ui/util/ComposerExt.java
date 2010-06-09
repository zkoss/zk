/* ComposerExt.java

	Purpose:
		
	Description:
		
	History:
		Thu Oct 25 19:37:27     2007, Created by tomyeh

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.util;

import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.metainfo.ComponentInfo;

/**
 * An addition interface implemented with {@link Composer} to provide
 * more control.
 *
 * <p>Note: any class that implements {@link ComposerExt} must implement
 * {@link Composer}, but not vice-versa.
 *
 * @author tomyeh
 * @since 3.0.0
 * @since Composer
 * @see FullComposer
 */
public interface ComposerExt {
	/** Invokes before composing a component.
	 * If you want to manipulate the specified component info,
	 * you can use {@link ComponentInfo#duplicate} to make a copy and then
	 * modify it such that it won't affect the default behavior.
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
	ComponentInfo compInfo) throws Exception;
	/** Invokes after the component is instantiated and initialized, but
	 * before composing any child.
	 * @param comp the component being composed
	 */
	public void doBeforeComposeChildren(Component comp) throws Exception;
	/** Called when an exception occurs when composing the component.
	 *
	 * <p>If you don't want to handle the exception, simply returns false.
	 * <code>boolean doCatch(Throwable ex) {return false;}</code>
	 *
	 * <p>An exception thrown in this method is simply logged. It has no
	 * effect on the execution.
	 * If you want to ignore the exception, just return true.
	 *
	 * @param ex the exception being thrown
	 * @return whether to ignore the exception. If false is returned,
	 * the exception will be re-thrown.
	 * Note: once a composer's doCatch returns true, the exception will be
	 * ignored and it means doCatch of the following composers won't be called.
	 */
	public boolean doCatch(Throwable ex) throws Exception;
	/** Called after the component has been composed completely.
	 * It is the last step of the composing.
	 *
	 * <p>Note: it is always called even if {@link #doCatch},
	 * {@link #doBeforeComposeChildren} or
	 * {@link Composer#doAfterCompose} is not called (due to exceptions).
	 *
	 * <p>Note: it is called after the onCreate event is posted,
	 * but before the onCreate and any other events are handled.
	 *
	 * <p>An exception thrown in this method is simply logged. It has no
	 * effect on the execution.
	 */
	public void doFinally() throws Exception;
}
