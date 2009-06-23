/* Applet.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Sep 19 17:32:48 TST 2008, Created by davidchen
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */

package org.zkoss.zul;

import java.io.StringWriter;
import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.zkoss.lang.Objects;
import org.zkoss.xml.HTMLs;

import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.ext.DynamicPropertied;
import org.zkoss.zul.impl.XulElement;
import org.zkoss.zk.au.out.AuInvoke;

/**
 * A generic applet component.
 * 
 * <p>
 * Non XUL extension.
 * 
 * @author Davidchen
 * @author Tomyeh
 * @since 3.6.0
 */
public class Applet extends HtmlBasedComponent implements DynamicPropertied,
org.zkoss.zul.api.Applet {
	private String _code = "";
	private String _codebase = "";
	private final Map _params = new LinkedHashMap();

	/** Return the code of the applet, i.e., the URI of the Java class.
	 */
	public String getCode() {
		return _code;
	}
	/** Sets the code of the applet, i.e., the URI of the Java class.
	 */
	public void setCode(String code) {
		_code = code;
		invalidate();
	}
	
	/** Return the code of the applet, i.e., the URI of the Java class.
	 * @since 3.6.2
	 */
	public String getCodebase() {
		return _codebase;
	}
	/** Sets the code of the applet, i.e., the URI of the Java class.
	 * @since 3.6.2
	 */
	public void setCodebase(String codebase) {
		_codebase = codebase;
		invalidate();
	}

	/** Sets a map of parameters (all existent parameters are removed first).
	 */
	public void setParams(Map params) {
		_params.clear();
		if (params != null)
			_params.putAll(params);
		invalidate();
	}
	/** Returns a map of parameters (never null).
	 */
	public Map getParams() {
		return _params;
	}
	/** Sets a parameter.
	 * If the value is null, the parameter is removed.
	 */
	public String setParam(String name, String value) {
		return value != null ? (String)_params.put(name, value):
			(String)_params.remove(name);
	}

	public Object getDynamicProperty(String name) {
		return _params.get(name);
	}

	public boolean hasDynamicProperty(String name) {
		return _params.containsKey(name);
	}

	public void setDynamicProperty(String name, Object value)
	throws WrongValueException {
		setParam(name, Objects.toString(value));
	}

	/** Invokes the function of the applet running at the client.
	 */
	public void invoke(String function) {
		response(null, new AuInvoke(this, "invoke", function));
	}
	/** Invokes the function of the applet running at the client with
	 * one argument.
	 */
	public void invoke(String function, String argument) {
		response(null, new AuInvoke(this, "invoke", function, argument));
	}
	/** Invokes the function of the applet running at the client with
	 * variable number argument.
	 */
	public void invoke(String function, String[] arguments) {
		final int len = arguments != null ? arguments.length: 0;
		final String[] args = new String[len + 1];
		args[0] = function;
		for (int j = 0; j < len; ++j)
			args[j + 1] = arguments[j];
		response(null, new AuInvoke(this, "invoke", args));
	}

	/** Sets the value of the specified filed.
	 */
	public void setField(String field, String value) {
		response(null, new AuInvoke(this, "setField", field, value));
	}
	/** No child is allowed.
	 */
	public boolean isChildable() {
		return false;
	}

	//super//
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer)
	throws java.io.IOException {
		super.renderProperties(renderer);

		render(renderer, "code", getCode());

		for (Iterator it = _params.entrySet().iterator(); it.hasNext();) {
			final Map.Entry me = (Map.Entry)it.next();
			render(renderer, "param",
				new String[] {(String)me.getKey(), (String)me.getValue()});
		}
	}
}
