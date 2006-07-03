/* Script.java

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
package com.potix.zk.ui.metainfo;

import java.net.URL;
import java.io.IOException;
import java.io.FileNotFoundException;

import com.potix.lang.D;
import com.potix.util.resource.ResourceCache;
import com.potix.util.resource.ContentLoader;

import com.potix.zk.ui.Page;
import com.potix.zk.ui.Component;
import com.potix.zk.ui.UiException;
import com.potix.zk.ui.util.Condition;

/**
 * Represents a BeanShell script
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class Script implements Condition {
	private final String _script;
	private final URL _url;
	private final Condition _cond;

	public Script(String script, Condition cond) {
		if (script == null)
			throw new NullPointerException();
		_script = script;
		_url = null;
		_cond = cond;
	}
	public Script(URL url, Condition cond) {
		if (url == null)
			throw new NullPointerException();
		_url = url;
		_script = null;
		_cond = cond;
	}
		
	/** Returns the script.
	 */
	public String getScript() throws IOException {
		if (_script != null)
			return _script;
		final Object o = getCache().get(_url);
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
		final StringBuffer sb = new StringBuffer(40).append("[Script: ");
		if (_url != null) {
			sb.append(_url);
		} else {
			final int len = _script.length();
			if (len > 20) {
				sb.append(_script.substring(0, 16)).append("...");
			} else {
				sb.append(_script);
			}
		}
		return sb.append(']').toString();
	}

	private static ResourceCache _cache;
	private static final ResourceCache getCache() {
		if (_cache == null) {
			synchronized (Script.class) {
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
