/* Bridge.java

	Purpose:
		
	Description:
		
	History:
		Thu Sep 16 10:49:20 TST 2010, Created by tomyeh

Copyright (C) 2010 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zkplus.embed;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.zkoss.json.JSONArray;
import org.zkoss.web.servlet.Charsets;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.http.ExecutionImpl;
import org.zkoss.zk.ui.http.WebManager;
import org.zkoss.zk.ui.sys.WebAppCtrl;

/**
 * Utilities to allow developers to start an execution in foreign Ajax channel.
 *
 * <pre><code>Bridge bridge = Bridge.start(svlctx, request, response, desktop);
 *try {
 *    //execution is activated and you could access anything belonging to the desktop
 *    String jscode = bridge.getResult();
 *    //send jscode back to the client to update DOM, if any
 *} finally {
 *    bridge.close(); //stop the execution
 *}</code></pre>
 *
 * <p>See also <a href="http://books.zkoss.org/wiki/ZK_Developer%27s_Reference/Integration/Start_Execution_in_Foreign_Ajax_Channel">Start Execution in Foreign Ajax Channel</a>
 *
 * @author tomyeh
 * @since 5.0.5
 */
public class Bridge {
	private Execution _exec;
	private Object _updctx;
	private Object _locale;

	/** Starts an execution.
	 * After returned, the execution is activated and the caller is free to access
	 * any components belonging the given desktop.
	 * <p>After processing, the caller shall invoke {@link #close} to stop
	 * the execution (in the finally clause).
	 * @param desktop the desktop you want to access.
	 * You could retrieve by use of {@link #getDesktop}.
	 */
	public static Bridge start(ServletContext svlctx, HttpServletRequest request, HttpServletResponse response,
			Desktop desktop) {
		try {
			return new Bridge(svlctx, request, response, desktop, Charsets.setup(request, response, "utf-8"));
		} catch (Exception ex) { //not possible
			throw UiException.Aide.wrap(ex);
		}
	}

	/** Returns the desktop of the given desktop ID, or null if not found.
	 * @param dtid the desktop's ID.
	 */
	public static Desktop getDesktop(ServletContext svlctx, HttpServletRequest request, String dtid) {
		return ((WebAppCtrl) WebManager.getWebApp(svlctx)).getDesktopCache(WebManager.getSession(svlctx, request))
				.getDesktop(dtid);
	}

	/** Constructor.
	 * Don't invoke this directly. Rather, use {@link #start} instead.
	 */
	protected Bridge(ServletContext svlctx, HttpServletRequest request, HttpServletResponse response, Desktop desktop,
			Object locale) throws Exception {
		_exec = new ExecutionImpl(svlctx, request, response, desktop, null);
		_updctx = ((WebAppCtrl) desktop.getWebApp()).getUiEngine().startUpdate(_exec);
		_locale = locale;
	}

	/** Returns the execution.
	 */
	public Execution getExecution() {
		return _exec;
	}

	/** Returns the result in the JavaScript.
	 * The caller shall send it back and evaluate it at the client (<code>eval(jscode);</code>).
	 * <p>After calling this method, the caller shall not modify the component's state any more.
	 */
	public String getResult() {
		final Desktop desktop = _exec.getDesktop();
		try {
			JSONArray result = ((WebAppCtrl) desktop.getWebApp()).getUiEngine().finishUpdate(_updctx);
			return new StringBuffer(512).append("zAu.doCmds('").append(desktop.getId()).append("',")
					.append(result.toString()).append(");").toString();
		} catch (Exception ex) { //not possible
			throw UiException.Aide.wrap(ex);
		}
	}

	/** Closes the execution such that other requests targeting
	 * the same desktop can be processed.
	 * It must be called. Otherwise, the whole desktop might not be able
	 * to be accessed any more.
	 */
	public void close() {
		final HttpServletRequest request = (HttpServletRequest) _exec.getNativeRequest();
		try {
			((WebAppCtrl) _exec.getDesktop().getWebApp()).getUiEngine().closeUpdate(_updctx);
			_updctx = null;
			_exec = null;
		} catch (Exception ex) { //not possible
			throw UiException.Aide.wrap(ex);
		} finally {
			Charsets.cleanup(request, _locale);
			_locale = null;
		}
	}
}
