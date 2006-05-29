/* ServletFns.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Apr 11 15:13:44     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.web.fn;

import java.io.Writer;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.ServletException;

import com.potix.util.prefs.Apps;

import com.potix.web.servlet.Servlets;
import com.potix.web.servlet.http.Encodes;
import com.potix.web.servlet.dsp.action.ActionContext;
import com.potix.web.servlet.auth.Authens;
import com.potix.web.el.ELContexts;
import com.potix.web.el.ELContext;

/**
 * Providing servlet relevant functions for EL.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.18 $ $Date: 2006/05/29 04:27:36 $
 */
public class ServletFns {
	protected ServletFns() {}

	/** Encodes a URL.
	 *
	 * <p>If an URI contains "*", it will be replaced with a proper Locale.
	 * For example, if the current Locale is zh_TW and the resource is
	 * named "ab*.cd", then it searches "ab_zh_TW.cd", "ab_zh.cd" and
	 * then "ab.cd", until any of them is found.
	 *
	 * <blockquote>Note: "*" must be right before ".", or the last character.
	 * For example, "ab*.cd" and "ab*" are both correct, while
	 * "ab*cd" and "ab*\/cd" are ignored.</blockquote>
	 *
	 * <p>If an URI contains two "*", the first "*" will be replaced with
	 * a browser code and the second with a proper locale.
	 * The browser code depends on what browser
	 * the user are used to visit the web site.
	 * Currently, the code for Internet Explorer is "ie", and all others
	 * are "moz".
	 * Thus, in the above example, if the resource is named "ab**.cd"
	 * and Firefox is used, then it searches "abmoz_zh_TW.cd", "abmoz_zh.cd"
	 * and then "abmoz.cd", until any of them is found.
	 */
	public static String encodeURL(String uri) throws ServletException {
		return Encodes.encodeURL(
			getCurrentServletContext(), getCurrentRequest(),
			getCurrentResponse(), uri);
	}

	/** Returns whether the current session has already been authenticated.
	 */
	public static boolean isAuthenticated() {
		return Authens.isAuthenticated(getCurrentRequest());
	}

	/** Returns whether the browser of the current request is explorer.
	 */
	public static boolean isExplorer() {
		return Servlets.isExplorer(getCurrentRequest());
	}
	/** Returns whether the browser of the current request is mozilla.
	 */
	public static boolean isMozilla() {
		return Servlets.isMozilla(getCurrentRequest());
	}

	/** Returns the current EL context. */
	public static ELContext getCurrentContext() {
		return ELContexts.getCurrent();
	}
	/** Returns the current output. */
	public static Writer getCurrentOut() throws IOException {
		return getCurrentContext().getOut();
	}
	/** Returns the current servlet context, or null if not available. */
	public static ServletContext getCurrentServletContext() {
		return getCurrentContext().getServletContext();
	}
	/** Returns the current servlet request, or null if not available. */
	public static ServletRequest getCurrentRequest() {
		return getCurrentContext().getRequest();
	}
	/** Returns the current servlet response, or null if not available. */
	public static ServletResponse getCurrentResponse() {
		return getCurrentContext().getResponse();
	}
		
	/** Renders the JSP fragment from EL.
	 *
	 * @param jf the JSP fragment; never null.
	 * @param out the ouput. If null, {@link ELContexts#getCurrent}'s getOut()
	 * is assumed. If {@link ELContexts#getCurrent} is null, jf's out is used.
	 */
	public static void render(JspFragment jf, Writer out)
	throws JspException, IOException {
		if (out == null) {
			//Note: we have to use out from the current context, because
			//jf might belong to includer (not the page invoking this method)
			//Eg, BorderTag pass getJspBody to the includee when invoke
			//this method.
			out = ELContexts.getCurrent().getOut();
		}
		jf.invoke(out);
	}

	/** Renders the DSP fragment from EL.
	 *
	 * @param ac the action context; never null.
	 */
	public static void render(ActionContext ac)
	throws ServletException, IOException {
		ac.renderFragment(null);
	}
}
