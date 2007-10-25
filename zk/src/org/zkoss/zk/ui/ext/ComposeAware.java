/* ComposeAware.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Oct 25 18:24:27     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.ext;

/**
 * Implemented by a component if it wants to 'intercept' the different phases
 * when it is 'composed' by the ZK loader.
 * By compose we mean, ZK loader creates and initializes a component
 * based on the definition in a ZUML page.
 *
 * <ol>
 * <li>ZK loader first creates the component (by use of
 * {@link org.zkoss.zk.ui.sys.UiFactory#newComponent}, which creates
 * and initializes the component accordingly).
 * <li>Invokes {@link #doBeforeComposeChildren}.</li>
 * <li>Composes all children, if any, of this component defined in
 * the ZUML page.</li>
 * <li>Invokes {@link #doAfterCompose} after all children are, if any,
 * composed.</li>
 * <li>Posts the onCreate event if necessary.</li>
 * </ol>
 *
 * <p>If you just want to intercept when the component is composed,
 * implements {@link AfterCompose} instead (since it is simpler).
 *
 * <p>To intercept the lifecycle of the creation of a page,
 * implement {@link org.zkoss.zk.ui.util.Initiator} and specify
 * the class with the init directive.
 *
 * @author tomyeh
 * @since 3.0.0
 * @see AfterCompose
 * @see org.zkoss.zk.ui.util.Initiator
 */
public interface ComposeAware {
	/** Invokes after the component is created and initialized, but
	 * before composing any child.
	 */
	public void doBeforeComposeChildren() throws Exception;
	/** Invokes after ZK loader creates this component,
	 * initializes it and composes all its children, if any.
	 */
	public void doAfterCompose() throws Exception;

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
	 * {@link #doBeforeComposeChildren} or {@link #doAfterCompose} is called.
	 *
	 * <p>Note: it is called before the onCreate event is handled.
	 */
	public void doFinally();
}
