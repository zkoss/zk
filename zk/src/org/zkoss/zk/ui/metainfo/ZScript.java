/* ZScript.java

	Purpose:
		
	Description:
		
	History:
		Thu Jun  2 10:46:03     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.metainfo;

import java.net.URL;

import org.zkoss.util.resource.ResourceCache;
import org.zkoss.util.resource.ContentLoader;
import org.zkoss.util.resource.Locator;
import org.zkoss.util.logging.Log;

import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.xel.ExValue;
import org.zkoss.zk.xel.EvaluatorRef;
import org.zkoss.zk.scripting.Interpreters;

/**
 * Represents a zscript content.
 *
 * @author tomyeh
 */
public class ZScript implements java.io.Serializable {
	private static final Log log = Log.lookup(ZScript.class);

	private EvaluatorRef _evalr;
	private String _zslang;
	private final String _cnt;
	/** An URL, an ExValue. */
	private final Object _url;
	private final Locator _locator;

	/** Parses the content into a {@link ZScript} instance.
	 *
	 * <p>This method assumes the content is in the following format:<br/>
	 * <code>lang:codes</code><br/>
	 * <code>codes</code>
	 *
	 * <p>For example, "javascript:var m = 0;" returns "javascript", while
	 * "var m = 0;" returns null.
	 *
	 * <p>Note: if the language doesn't exist, null is returned.
	 * Reason: the above syntax may be conflict with some scripting languages.
	 *
	 * <p>Note: no space is allowed.
	 *
	 * @param content the content of zscript codes
	 * @since 3.0.0
	 */
	public static final ZScript parseContent(String content) {
		return parseContent(content, 0);
	}
	/** Parses the content into a {@link ZScript} instance.
	 *
	 * <p>It is similar to {@link #parseContent(String)} except it
	 * allows the caller to specify the line number of the first line of the
	 * content.
	 *
	 * @param content the content of zscript codes
	 * @param lineno the linenumber of the first line. Ignored if
	 * zero (or negative).
	 * @since 3.6.1
	 */
	public static final ZScript parseContent(String content, int lineno) {
		String prefix = null, zslang = null;
		final int len = content != null ? content.length(): 0;
		if (len > 0) {
			//Don't generate prefix if content is empty (i.e., keep empty)
			//so PageImpl.interpret() could optimize it (not to execute at all)
			if (lineno > 1) {
				final StringBuffer sb = new StringBuffer(lineno);
				while (--lineno > 0)
					sb.append('\n');
				prefix = sb.toString();
			}

			for (int j = 0; j < len; ++j) {
				final char cc = content.charAt(j);
				if (cc == ':') {
					if (j > 0) {
						zslang = content.substring(0, j);
						if (Interpreters.exists(zslang)) {
							content = content.substring(j + 1);
							break;
						} else {
							log.warning("Ignored: unknown scripting language, "+zslang);
						}
					}
					break;
				} if (!Interpreters.isLegalName(cc)) {
					break; //done
				}
			}
		}
		return new ZScript(zslang, prefix != null && content.length() > 0 ? prefix+content: content);
	}

	/** Creates a zscript with the content directly.
	 *
	 * @param zslang the scripting language. If null, it is the same as
	 * {@link org.zkoss.zk.ui.Page#getZScriptLanguage}.
	 * @param content the zscript content
	 * @see #parseContent
	 */
	public ZScript(String zslang, String content) {
		_zslang = zslang;
		_cnt = content != null ? content: "";
		_url = null;
		_locator = null;
	}
	/** Creates a zscript with an URL that is used to load the content.
	 */
	public ZScript(String zslang, URL url) {
		if (url == null)
			throw new IllegalArgumentException("null");

		_zslang = zslang;
		_url = url;
		_cnt = null;
		_locator = null;
	}
	/** Creates a zscript with an URL that is used to load the content.
	 * @param evalr the evaluator used to evaluate
	 * @exception IllegalArgumentException if url or locator is null, or
	 * url contains EL and evalr is null.
	 */
	public ZScript(EvaluatorRef evalr, String zslang, String url, Locator locator) {
		if (url == null || locator == null)
			throw new IllegalArgumentException("null");

		//TODO: use url's extension to determine zslang
		_evalr = evalr;
		_zslang = zslang;
		_url = new ExValue(url, String.class);
		_cnt = null;
		_locator = locator;

		if (evalr == null && ((ExValue)_url).isExpression())
			throw new IllegalArgumentException("evalr required since EL is used: "+url);
	}

	/** Returns the scripting language, or null if the default scripting language
	 * is preferred.
	 */
	public String getLanguage() {
		return _zslang;
	}
	/** Sets the scripting language.
	 *
	 * @param zslang the scripting language. If null, the default scripting
	 * language is assume.
	 */
	public void setLanguage(String zslang) {
		_zslang = zslang;
	}
	/** Returns the raw content.
	 * It is the content specified in the contructor
	 * ({@link #ZScript(String, String)}.
	 * If URL is specified in the contructor, null is returned.
	 *
	 * <p>On the other hand, {@link #getContent} will load the content
	 * automatically if URL is specified.
	 *
	 * @return the raw content specified in the contructor, or null
	 * if URL is specified instead.
	 */
	public String getRawContent() {
		return _cnt;
	}
	/** Returns the content of zscript.
	 * If URL is specified, this method loads the content from it.
	 * If URL is an EL expression, it will be evaluated first.
	 *
	 * @param page the page when this zscript is interpreted.
	 * Used only if this object is contructed with {@link #ZScript(EvaluatorRef, String, String, Locator)}.
	 * @param comp the component when this zscript is interpreted.
	 * Used only if this object is contructed with {@link #ZScript(EvaluatorRef, String, String, Locator)}.
	 * @exception UiException if faied to load the content
	 */
	public String getContent(Page page, Component comp) {
		if (_cnt != null)
			return _cnt;

		final URL url;
		if (_url instanceof ExValue) {
			final String s = (String)(comp != null ? 
				((ExValue)_url).getValue(_evalr, comp):
				((ExValue)_url).getValue(_evalr, page));
			if (s == null || s.length() == 0)
				throw new UiException("The zscript URL, "+_url+", is evaluated to \""+s+'"');
			url = _locator.getResource(s);
			if (url == null)
				throw new UiException("File not found: "+s+" (evaluated from "+_url+')');
				//note: we don't throw FileNotFoundException since Tomcat 'eats' it
		} else {
			url = (URL)_url;
		}

		final String o = getCache().get(url);
			//It is OK to use cache here even if script might be located, say,
			//at a database. Reason: it is Locator's job to implement
			//the relevant function for URL (including lastModified).
		if (o == null)
			throw new UiException("File not found: "+_url);
			//note: we don't throw FileNotFoundException since Tomcat 'eats' it
		return o;
	}

	/** Sets the evaluator reference.
	 */
	/*pacakge*/ void setEvaluatorRef(EvaluatorRef evalr) {
		_evalr = evalr;
	}

	//Object//
	public String toString() {
		final StringBuffer sb = new StringBuffer(40).append("[zscript: ");
		if (_url != null) {
			sb.append(_url);
		} else {
			final int len = _cnt.length();
			if (len > 20) {
				sb.append(_cnt.substring(0, 16)).append("...");
			} else {
				sb.append(_cnt);
			}
		}
		return sb.append(']').toString();
	}

	private static ResourceCache<Object, String> _cache;
	private static final ResourceCache<Object, String> getCache() {
		if (_cache == null) {
			synchronized (ZScript.class) {
				if (_cache == null) {
					final ResourceCache<Object, String> cache
						= new ResourceCache<Object, String>(new ContentLoader());
					cache.setMaxSize(512);
					cache.setLifetime(60*60*1000); //1hr
					_cache = cache;
				}
			}
		}
		return _cache;
	}
}
