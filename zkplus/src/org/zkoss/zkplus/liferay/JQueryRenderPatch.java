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
	 * <p>Default: "500" (it means 500 milliseconds)
	 *
	 * <p>You can specify a number to indicate how many milliseconds to wait
	 * before replacing with the correct content.
	 * If negative, the patch is ignored.
	 */
	public static final String JQUERY_PATCH = "org.zkoss.zkplus.liferary.jQueryPatch";

	private int _delay = -1;

	public JQueryRenderPatch() {
		final String val = Library.getProperty(JQUERY_PATCH);
		try {
			_delay = Integer.parseInt(val);
		} catch (Throwable ex) {
			log.warning("Ignored delay time specified in "+JQUERY_PATCH+": "+val);
		}
	}
	/** Returns the number of milliseconds to wait before replacing with
	 * the correct content.
	 * <p>Default: depends on the value defined in the {@link #JQUERY_PATCH}
	 * library property.
	 */
	public int getDelay() {
		return _delay;
	}
	/** Sets the number of milliseconds to wait before replacing with
	 * the correct content.
	 * @see #JQUERY_PATCH
	 */
	public void setDelay(int delay) {
		_delay = delay;
	}

	//@Override
	/** It returns an instance of StringWriter if {@link #getDelay} is non-negative,
	 * or null if negative (means no patch).
	 */
	public Writer beforeRender(RequestInfo reqInfo) {
		return _delay >= 0 ? new StringWriter(): null;
			//we cannot retrieve HTTP request's header so no need to
			//apply the patch for particular browsers, such as ie
	}
	//@Override
	public void patchRender(RequestInfo reqInfo, Page page, Writer result, Writer out)
	throws IOException {
		final String extid = page.getUuid() + "-ext";
		out.write("<div id=\"");
		out.write(extid);
		out.write("\"></div><script>setTimeout(function(){\njQuery('#");
		out.write(extid);
		out.write("').append('");
			//we have to use append() since it is evaluated synchronously
			//while replaceWith() is not
		out.write(Strings.escape(((StringWriter)result).toString(), Strings.ESCAPE_JAVASCRIPT));
		out.write("');},");
		out.write(_delay);
		out.write(");</script>");
	}
}
