/* ServletFns.java

	Purpose:
		
	Description:
		
	History:
		Mon Apr 11 15:13:44     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.web.fn;

import java.io.Writer;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.ServletException;

import org.zkoss.web.servlet.Servlets;
import org.zkoss.web.servlet.http.Encodes;
import org.zkoss.web.servlet.dsp.DspException;
import org.zkoss.web.servlet.dsp.action.ActionContext;
import org.zkoss.web.servlet.xel.RequestContext;
import org.zkoss.web.servlet.xel.RequestContexts;

/**
 * Providing servlet relevant functions for EL.
 *
 * @author tomyeh
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
	 * Currently, the code for Internet Explorer is "ie", Safari is "saf",
	 * Opera is "opr" and all others are "moz".
	 * Thus, in the above example, if the resource is named "ab**.cd"
	 * and Firefox is used, then it searches "abmoz_zh_TW.cd", "abmoz_zh.cd"
	 * and then "abmoz.cd", until any of them is found.
	 */
	public static String encodeURL(String uri) throws ServletException {
		return Encodes.encodeURL(
			getCurrentServletContext(), getCurrentRequest(),
			getCurrentResponse(), uri);
	}

	/** Returns whether the current request is from
	 * a browser of the specified type.
	 *
	 * @param type the type of the browser.
	 * Allowed values include "robot", "ie", "ie6", "ie6-", "ie7", "ie8",
	 * "ie7-", "gecko", "gecko2", "gecko3", "gecko2-",
	 * "opara", "safari",
	 * "mil", "hil", "mil-".<br/>
	 * Note: "ie6-" means Internet Explorer 6 only; not Internet Explorer 7
	 * or other.
	 * @since 3.5.1
	 */
	public static boolean isBrowser(String type) {
		return Servlets.isBrowser(getCurrentRequest(), type);
	}
	/** Returns whether the browser of the current request is Explorer.
	 */
	public static boolean isExplorer() {
		return Servlets.isExplorer(getCurrentRequest());
	}
	/** Returns whether the browser of the current request is Explorer 7 or later.
	 */
	public static boolean isExplorer7() {
		return Servlets.isExplorer7(getCurrentRequest());
	}
	/** Returns whether the browser of the current request is Gecko based,
	 * such as Mozilla, Firefox and Camino.
	 */
	public static boolean isGecko() {
		return Servlets.isGecko(getCurrentRequest());
	}
	/** Returns whether the browser is Gecko 3 based, such as Firefox 3.
	 * @since 3.5.0
	 */
	public static boolean isGecko3() {
		return Servlets.isGecko3(getCurrentRequest());
	}
	/** Returns whether the browser of the current request is Safari.
	 */
	public static boolean isSafari() {
		return Servlets.isSafari(getCurrentRequest());
	}
	/** Returns whether the browser of the current request is Opera.
	 */
	public static boolean isOpera() {
		return Servlets.isOpera(getCurrentRequest());
	}
	/** Returns whether the operation system of the browser is Ubuntu.
	 */
	public static boolean isUbuntu() {
		return Servlets.isUbuntu(getCurrentRequest());
	}

	/** Returns the current EL context. */
	public static RequestContext getCurrentContext() {
		return RequestContexts.getCurrent();
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
		
	/** Renders the DSP fragment from EL.
	 *
	 * @param ac the action context; never null.
	 */
	public static void render(ActionContext ac)
	throws DspException, IOException {
		ac.renderFragment(null);
	}
}
