/* ServletFns.java

	Purpose:
		
	Description:
		
	History:
		Mon Apr 11 15:13:44     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.web.fn;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.zkoss.lang.Library;
import org.zkoss.lang.Strings;
import org.zkoss.web.servlet.dsp.DspException;
import org.zkoss.web.servlet.dsp.action.ActionContext;
import org.zkoss.web.servlet.http.Encodes;
import org.zkoss.web.servlet.xel.RequestContext;
import org.zkoss.web.servlet.xel.RequestContexts;
import org.zkoss.web.theme.StandardTheme;
import org.zkoss.web.theme.StandardTheme.ThemeOrigin;
import org.zkoss.web.theme.Theme;

/**
 * Providing servlet relevant functions for EL.
 *
 * @author tomyeh
 */
public class ServletFns {
	protected ServletFns() {
	}

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
		return Encodes.encodeURL(getCurrentServletContext(), getCurrentRequest(), getCurrentResponse(), uri);
	}

	/**
	 * Encodes a URL with theme key injected. 
	 * @param s the string to encode
	 * @return the encoded string or null if s is null
	 * @throws UnsupportedEncodingException 
	 * @since 6.0.0
	 */
	public static final String encodeThemeURL(String s) throws ServletException {
		if (s == null)
			return null;

		return encodeURL(resolveThemeURL(s));
	}

	private static final String THEME_FOLDER_ROOT = "org.zkoss.theme.folder.root";

	/**
	 * Resolves a URL to point to resource served by the current theme. 
	 * @param url the default theme url to resolve
	 * @return the resolved url or null if url is null
	 * @since 6.5.2
	 */
	public static final String resolveThemeURL(String url) {
		if (url == null)
			return null;

		String themeName = ThemeFns.getCurrentTheme();
		String prefix = Library.getProperty(THEME_FOLDER_ROOT, "theme");

		String resolved = null;

		if (Strings.isBlank(themeName) || StandardTheme.DEFAULT_NAME.equals(themeName))
			resolved = url;
		else {
			Theme theme = ThemeFns.getThemeRegistry().getTheme(themeName);
			if (theme instanceof StandardTheme) {
				if (((StandardTheme) theme).getOrigin() == ThemeOrigin.JAR)
					resolved = url.replaceFirst("~./", "~./" + themeName + "/");
				else
					resolved = url.replaceFirst("~./", "/" + prefix + "/" + themeName + "/");
			}
		}
		return resolved;
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
	public static void render(ActionContext ac) throws DspException, IOException {
		ac.renderFragment(null);
	}
}
