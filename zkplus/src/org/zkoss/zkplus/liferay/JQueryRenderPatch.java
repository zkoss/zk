/* JQueryRenderPatch.java

	Purpose:
		
	Description:
		
	History:
		Sun Jan 17 11:48:04 TST 2010, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zkplus.liferay;

import java.io.Writer;
import java.io.StringWriter;
import java.io.IOException;

import javax.servlet.ServletRequest;

import org.zkoss.lang.Library;
import org.zkoss.lang.Strings;
import org.zkoss.util.logging.Log;
import org.zkoss.web.servlet.Servlets;

import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.sys.RequestInfo;
import org.zkoss.zk.ui.sys.PageRenderPatch;

/**
 * Used to patch the rendering result of a ZK portlet for Liferay.
 * When using ZK portlets with Liferay under Internet Explorer, we have
 * to delay the processing at the client a bit.
 *
 * <p>To use it, you have to specify a library proeprty called
 * ""org.zkoss.zk.portlet.PageRenderPatch.class" with this class's name
 * ("org.zkoss.zkplus.liferay.JQueryRenderPatch").
 *
 * <p>You can further control the behavior of this patch by use of
 * a library property called "org.zkoss.zkplus.liferary.jQueryPatch"
 * (refer to {@link #JQUERY_PATCH} for details).
 *
 * @author tomyeh
 * @since 5.0.0
 */
public class JQueryRenderPatch implements PageRenderPatch {
	private static final Log log = Log.lookup(JQueryRenderPatch.class);

	/** A library property to indicate how to apply the so-called jQuery patch.
	 * <p>Default: "ie"
	 *
	 * <p>If the value is "ie", it is applied only to Internet Explorer.
	 * Refer to {@link org.zkoss.web.servlet.Servlets#isBrowser} for more options.
	 * If the value is "*", it is applied to all kinds of browsers.
	 * <p>If you want to specify the number of milliseconds to wait before
	 * replacing with the correct content, you could append a number
	 * by separating it with comma. For example, "ie,1000" means to wait
	 * 1000 miliseconds if the browser is Internet Explorer.
	 * The default is 500 miliseconds.
	 */
	public static final String JQUERY_PATCH = "org.zkoss.zkplus.liferary.jQueryPatch";

	private String _browser;
	private int _delay;

	public JQueryRenderPatch() {
		_delay = 500;
		_browser = Library.getProperty(JQUERY_PATCH);
		if (_browser != null) {
			final int j = _browser.indexOf(',');
			if (j >= 0) {
				final String v = _browser.substring(j + 1);
				try {
					_delay = Integer.parseInt(v);
					if (_delay < 0)
						_delay = 0;
				} catch (Throwable ex) {
					log.warning("Ignored delay time specified in "+JQUERY_PATCH+": "+_browser);
				}
				_browser = _browser.substring(0, j);
			}
			if (_browser.length() == 0)
				_browser = null;
		}
	}
	//@Override
	public Writer beforeRender(RequestInfo reqInfo) {
		return _browser != null && ("*".equals(_browser)
		|| Servlets.isBrowser((ServletRequest)reqInfo.getNativeRequest(), _browser)) ?
			new StringWriter(): null;
	}
	//@Override
	public void patchRender(RequestInfo reqInfo, Page page, Writer result, Writer out)
	throws IOException {
		out.write("<div id=\"");
		out.write(page.getUuid());
		out.write("\"></div><script>setTimeout(function(){\njQuery('#");
		out.write(page.getUuid());
		out.write("').replaceWith('");
		out.write(Strings.escape(((StringWriter)result).toString(), Strings.ESCAPE_JAVASCRIPT));
		out.write("');},");
		out.write(_delay);
		out.write(");</script>");
	}
}
