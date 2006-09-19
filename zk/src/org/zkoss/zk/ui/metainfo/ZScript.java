/* ZScript.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Jun  2 10:46:03     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.metainfo;

import java.net.URL;
import java.io.IOException;
import java.io.FileNotFoundException;

import org.zkoss.lang.D;
import org.zkoss.util.resource.ResourceCache;
import org.zkoss.util.resource.ContentLoader;
import org.zkoss.util.resource.Locator;

import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.util.Condition;

/**
 * Represents a zscript.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class ZScript implements Condition {
	private final String _cnt;
	private final Object _url;
	private final Locator _locator;
	private final Condition _cond;

	/** Creates a zscript object with the content directly.
	 *
	 * @param content the zscript content
	 */
	public ZScript(String content, Condition cond) {
		_cnt = content;
		_url = null;
		_locator = null;
		_cond = cond;
	}
	/** Create a zscript object with an URL that is used to load the content.
	 *
	 * @param url the URL to load the content of zscript.
	 */
	public ZScript(URL url, Condition cond) {
		if (url == null)
			throw new IllegalArgumentException("null");
		_url = url;
		_cnt = null;
		_locator = null;
		_cond = cond;
	}
	/** Constructs a {@link ZScript} with an URL, which might contain an EL
	 * expression.
	 *
	 * @param locator the locator used to locate the zscript file
	 */
	public ZScript(String url, Condition cond, Locator locator) {
		if (url == null || locator == null)
			throw new IllegalArgumentException("null");

		_url = url;
		_cnt = null;
		_locator = locator;
		_cond = cond;
	}

	/** Returns the script.
	 * <p>Note: before evaluating the returned script, you have to invoke
	 * {@link #isEffective(Component)} or {@link #isEffective(Page)} first.
	 *
	 * @param page the page when this zscript is interpreted.
	 * Used only if this object is contructed with {@link #ZScript(String, Condition, Locator)}.
	 * @param comp the component when this zscript is interpreted.
	 * Used only if this object is contructed with {@link #ZScript(String, Condition, Locator)}.
	 */
	public String getContent(Page page, Component comp) throws IOException {
		if (_cnt != null)
			return _cnt;

		final URL url;
		if (_locator != null) {
			final String expr = (String)_url;
			final String s =
				expr.indexOf("${") < 0 ? expr:
				comp != null ?
					(String)Executions.evaluate(comp, expr, String.class):
					(String)Executions.evaluate(page, expr, String.class);
			if (s == null || s.length() == 0)
				throw new UiException("The zscript URL, "+_url+", is evaluated to \""+s+'"');
			url = _locator.getResource(s);
			if (url == null)
				throw new FileNotFoundException("File not found: "+s+" (evaluated from "+_url+')');
		} else {
			url = (URL)_url;
		}

		final Object o = getCache().get(url);
			//It is OK to use cache here even if script might be located, say,
			//at a database. Reason: it is Locator's job to implement
			//the relevant function for URL (including lastModified).
		if (o == null)
			throw new FileNotFoundException("File not found: "+_url);
		if (!(o instanceof String))
			throw new IOException("Illegal file type: "+o.getClass());
		return (String)o;
	}

	//Condition//
	public boolean isEffective(Component comp) {
		return _cond == null || _cond.isEffective(comp);
	}
	public boolean isEffective(Page page) {
		return _cond == null || _cond.isEffective(page);
	}

	//Object//
	public String toString() {
		final StringBuffer sb = new StringBuffer(40).append("[ZScript: ");
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

	private static ResourceCache _cache;
	private static final ResourceCache getCache() {
		if (_cache == null) {
			synchronized (ZScript.class) {
				if (_cache == null) {
					final ResourceCache cache
						= new ResourceCache(new ContentLoader());
					cache.setMaxSize(250).setLifetime(60*60000); //1hr
					_cache = cache;
				}
			}
		}
		return _cache;
	}
}
