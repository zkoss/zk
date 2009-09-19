/* RendererCtrl.java

	Purpose:
		
	Description:
		
	History:
		Fri Aug 19 23:44:56     2005, Created by tomyeh

Copyright (C) 2004 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

/**
 * This interface defines the methods components like {@link Listbox}
 * use to notify the renderer for several circumstance.
 *
 * <p>Though {@link ListitemRenderer#render} is called one item a timer,
 * a request might have several items to render. And, if the renderer
 * implements this interface, {@link #doTry} will be called before
 * any redering, and {@link #doFinally} will be caleld after all rendering.
 * If any exception occurs, {@link #doCatch} will be called.
 *
 * <p>A typical use is to start a transaction and use it for rendering
 * all items from the same request.
 * 
 * @author tomyeh
 */
public interface RendererCtrl {
	/** Called before rendering any item.
	 *
	 * <p>Example, you could start an transaction here.
	 */
	public void doTry();
	/** Called if any exception occurs when rendering items.
	 * Note: this method won't be called if exception occurs in {@link #doTry}.
	 *
	 * <p>If this method doesn't throw exception, ex is 'eaten' though
	 * rendering is terminating.
	 * Thus, if you want to bubble up the exception, you could throw it
	 * again.
	 *
	 * <p>Example, you could roll back the transaction.
	 */
	public void doCatch(Throwable ex) throws Throwable;
	/** Invoked after all rendering are done successfully or an exception
	 * occurs.
	 *
	 * <p>If an exception occurs, {@link #doCatch} will be invoked first and
	 * then {@link #doFinally}.
	 */
	public void doFinally();
}
