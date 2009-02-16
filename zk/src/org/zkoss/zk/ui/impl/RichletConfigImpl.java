/* RichletConfigImpl.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Oct  5 15:24:09     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.impl;

import java.util.Map;
import java.util.Iterator;

import org.zkoss.util.CollectionsX;

import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.RichletConfig;

/**
 * An implementation of {@link RichletConfig}.
 *
 * @author tomyeh
 */
public class RichletConfigImpl implements RichletConfig {
	private final WebApp _wapp;
	private final Map _params;

	/**
	 * @param wapp the {@link WebApp} that this config belongs to (never null).
	 * @param params the initail parameters. Empty is assumed if null.
	 */
	public RichletConfigImpl(WebApp wapp, Map params) {
		if (wapp == null)
			throw new IllegalArgumentException("null");
		_wapp = wapp;
		_params = params == null || params.isEmpty() ? null: params;
	}

	//RichletConfig//
	public WebApp getWebApp() {
		return _wapp;
	}
	public String getInitParameter(String name) {
		return _params != null ? (String)_params.get(name): null;
	}
	public Iterator getInitParameterNames() {
		return _params != null ? _params.keySet().iterator():
			CollectionsX.EMPTY_ITERATOR;
	}
}
