/* DesktopRecycle.java

	Purpose:
		
	Description:
		
	History:
		Thu Dec  3 19:15:50 TST 2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zk.ui.util;

import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Desktop;

/**
 * Used to recycle the desktop ({@link Desktop}). By recycling the desktop,
 * we mean to re-use the same desktop for the same URI if applicable.
 * For example, if an user navigates to other URL and navigates back, the application
 * can re-use the same desktop (by implementing this interface).
 * It is useful if the page takes a lot of time to prepare.
 *
 * <p>A typical implementation (such as {@link org.zkoss.zkmax.ui.util.DesktopRecycle})
 * caches the desktop when {@link #beforeRemove} is called,
 * and then return it if {@link #beforeService} matches it.
 *
 * <p>Refer to <a href="http://docs.zkoss.org/wiki/Performance_Tip#Reuse_Desktops">Performance Tip</a>
 * for more information.
 *
 * @author tomyeh
 * @since 5.0.0
 */
public interface DesktopRecycle {
	/** Called when the client asks the server to remove a deskdop 
	 * because of the user's navigating to other URL or refreshing the page.
	 *
	 * <p>The implementation can retrieve the request path by calling
	 * {@link Desktop#getRequestPath}.
	 *
	 * <p>Notice that, when the user refreshes a page, this method might be
	 * called after {@link #beforeService} is called (depending on the browser
	 * and networking). In other words, there is no way it is a brand-new request
	 * from a new window, or it is caused by user's refreshing.
	 *
	 * @param exec the current execution.
	 * @param cause the cause. It is reserved for the future extension.
	 * @return whether to recycle the desktop. If true is returned, the desktop
	 * won't be removed. It is the implementation's job to remove it
	 * if necessary.
	 */
	public boolean beforeRemove(Execution exec, Desktop desktop, int cause);
	/** Called after a desktop is removed.
	 * A desktop is removed for different reasons, such as a sesstion timeout,
	 * and too many desktops (when the memory is running low).
	 * In other words, some desktops are removed directly and only
	 * {@link #afterRemove} is called.
	 * <p>The implementation has to remove the cached desktop, if any.
	 * <p>When this method is called, there might be no execution available
	 * ({@link org.zkoss.zk.ui.Executions#getCurrent} might return null).
	 *
	 * @param sess the session that the desktop belongs to.
	 * Notice that {@link Desktop#getSession} might be null when this method
	 * is called since it is destroyed.
	 * @param desktop the desktop to remove
	 */
	public void afterRemove(Session sess, Desktop desktop);

	/** Called when an user requests the content of a page.
	 * To reuse a desktop, this method has to return the desktop to reuse.
	 *
	 * @param exec the execution. It doesn't not associated with
	 * a desktop, and used only for accessing the request and response.
	 * @param uri the request URI. It is
	 * a combination of {@link Desktop#getRequestPath} and {@link Desktop#getQueryString}.
	 * For example, if {@link Desktop#getRequestPath} and {@link Desktop#getQueryString}
	 * are "/foo.zul" and "abc=1", then this parameter is "/foo.zul?abc=1".
	 * @return the desktop to re-use. If null is returned, ZK Loader will
	 * create a desktop as if this interface is not installed.
	 */
	public Desktop beforeService(Execution exec, String uri);
	/** Called after the request is served.
	 * <p>The implementation usually does nothing in this method, unless
	 * it wants to re-use it no matter if the same user might open two or more
	 * windows to visit the same URL.
	 *
	 * <p>When this method is called, there might be no execution available
	 * ({@link org.zkoss.zk.ui.Executions#getCurrent} might return null).
	 */
	public void afterService(Desktop desktop);
}
