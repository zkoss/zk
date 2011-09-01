/* FailoverManager.java

	Purpose:
		
	Description:
		
	History:
		Wed Mar 28 14:39:26     2007, Created by tomyeh

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.sys;

import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.UiException;

/**
 * Represents a class that is able to handle fail-over in the
 * application specific way.
 *
 * <p>Desktops, pages and components are serializables, so you can use
 * the clustering feature supported by the Web server without implementing
 * this interface. In other words, you need to implement this interface
 * only if you want to provide an application-specific way to re-create
 * back.
 *
 * <p>If you want to use the Web server's clustering feature,
 * what you need to do is to specify {@link org.zkoss.zk.ui.http.SerializableUiFactory}
 * as the UI factory in zk.xml as follows (and forget {@link FailoverManager}).
 *
 * <pre><code>
&lt;system-config&gt;
 &lt;ui-factory-class&gt;org.zkoss.zk.ui.http.SerializableUiFactory&lt;/ui-factory-class&gt;
&lt;/system-config&gt;
</code></pre>
 *
 * <p>The recovery involves two phases:
 *<ol>
 *<li>{@link #isRecoverable}: It tests whether the desktop is recoverable.</li>
 *<li>{@link #recover}: If recoverable,
 * ZK will create the desktop accordingly, prepare the execution
 * and then invoke this method to do real recovery.<li>
 *</ol>
 *
 * @author tomyeh
 */
public interface FailoverManager {
	/** Starts the failover manager.
	 * @since 5.0.0
	 */
	public void start(WebApp wapp);
	/** Stops the failover manager.
	 * Called only if the server is about to stop.
	 * @since 5.0.0
	 */
	public void stop(WebApp wapp);

	/** Tests whether the specified desktop ID is recoverable.
	 *
	 * <p>Note: when this method called, no execution
	 * ({@link Execution}) is available.
	 * The implementation shall only check whether it is possible to
	 * recover the specified desktop ID.
	 * Then, do the real recovery in {@link #recover}.
	 *
	 * @param sess the session
	 * @param desktopId the desktop ID to recover
	 */
	public boolean isRecoverable(Session sess, String desktopId);
	/** Recovers the specified desktop.
	 * It is called only when {@link #isRecoverable} returns true.
	 * Before calling this method, the desktop and execution
	 * {@link Execution}) are all prepared.
	 *
	 * <p>Note: ZK assumes the failover manager can recover the desktop
	 * completely, so it won't update the browser whatever have been done
	 * in this method.
	 *
	 * <p>During the recovering process, you have to do the following:
	 *
	 * <p><b>Recover Desktop</b></p>
	 * <ul>
	 * <li>[must] Call {@link DesktopCtrl#setId} to correct the desktop ID.</li>
	 * <li>[optional] Call {@link Desktop#setCurrentDirectory} to
	 * correct the current directory.
	 * It is the directory of the path of the ZUML page.</li>
	 * <li>[optional] Call {@link Desktop#setDeviceType} to correct
	 * the device type (default: html).</li>
	 * <li>[Must] Recover all pages and components.</li>
	 * <li>[Optional] Recover the response ID by use of
	 * {@link DesktopCtrl#setResponseId}.</li>
	 * <li>[Optional] Recover desktop's attributes.</li>
	 * </ul>
	 *
	 * <p><b>Recover Page</b></p>
	 * <ul>
	 * <li>[Must] Invoke {@link org.zkoss.zk.ui.impl.PageImpl#PageImpl(org.zkoss.zk.ui.metainfo.LanguageDefinition, org.zkoss.zk.ui.metainfo.ComponentDefinitionMap, String, String)}
	 * (or your own implementaton) to create an instance of {@link org.zkoss.zk.ui.Page}.</li>
	 * <li>[Must] Then, invoke {@link org.zkoss.zk.ui.sys.PageCtrl#init} to initialized the page.</li>
	 * <li>[Optional] Recover page's attributes.</li>
	 * </ul>
	 *
	 * @exception UiException if failed to recover
	 */
	public void recover(Session sess, Execution exec, Desktop desktop)
	throws UiException;
}
